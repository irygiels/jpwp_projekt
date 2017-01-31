package cisco.demo;

/**
 * Created by irygiels on 01.03.16.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;


public class BeaconFinder extends AppCompatActivity implements Caller {

    private final String SERVER_ADDRESS = "https://cisco.com"; //adres serwera - do wpisania
    private final Double time = 0.05;
    private final long MINUTE = 60*1000;
    private Region ourRegion; //potrzebne do inicjalizacji szukania beaconow
    private BeaconManager beaconManager; //potrzebny do interpretowania danych z beacona
    private Context that;
    public int n;
    public List<String> allBeaconsInRangeSend;
    private String address;
    private JSONObject data; //jesli dane splywaja w json

    private volatile boolean threadsShouldBeRunning = true; //czy wszystko ok - boolean

    private Button refreshButton; //guzik checkin

    private Map<String, Timestamp> currentBeacons;
    private Map<String, List<RowBean>> macBeacons;
    private Map<String, String> distanceBeacons;

    public ListView listView1; //lista, ktora bedzie sie updateowac
    private final ArrayList<String> dataList = new ArrayList<String>(30);

    public BeaconFinder(Context context) {
    }
    public BeaconFinder(){ getDataLength(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar barHide = getSupportActionBar();
        if (barHide != null) {
            barHide.hide();
        }

        setContentView(R.layout.activity_ranging_and_displaying); //ustawienie layoutu

        currentBeacons = new HashMap<String, Timestamp>();
        macBeacons = new HashMap<String, List<RowBean>>();
        distanceBeacons = new HashMap<String, String>();

        beaconManager = new BeaconManager(this); //rozpocznij nasluchiwanie
        ourRegion = new Region("region", null, null, null); //w regionie (niezbedne do rozpoczecia)

        listView1 = (ListView) findViewById(R.id.Lista); //lista zawarta w layoucie
        refreshButton = (Button) findViewById(R.id.button);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //userList();
                updateView();
                //jesli kliknales juz 4 razy, zacznij kolejne activity, w ktorym bedzie pokazywany distance
            }
        });


    }

    //rozpoczyna szukanie w okolicy po starcie activity
    @Override
    protected void onStart() {
        super.onStart();
        startRangingBeacons(); //listener do beaconow, dopisuje je do tablicy
        cleanOldBeaconsAfter(time); //wyczysc liste beaconow po jakims czasie (do ustawienia wyzej)

    }


    //przy zatrzymaniu activity
    @Override
    protected void onStop() {
        super.onStop();
        threadsShouldBeRunning = false; //nie ok, wylaczam funkcjonalnosci
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ranging_and_displaying, menu); //ustawienie layoutu
        return true;
    }

    //zarzadzanie lista
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //tutaj na podstawie wlasnosci beacona (major+minor) tworze jego identyfikator
    private String makeKey(Beacon beacon) {
        //
        //final String key = Integer.toString(beacon.getMajor()) + Integer.toString(beacon.getMinor());
        final String key = String.valueOf(beacon.getMacAddress());
        return key;
    }

    private String getDistance(Beacon beacon) {
        double distance = Math.min(Utils.computeAccuracy(beacon), 10.0); //do 10 m
        String wyn = String.format("%.2f", distance);
        return wyn;
    }

    private void startRangingBeacons() {

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                for (Beacon beacon : beacons) {
                    String key = makeKey(beacon); //identyfikator beacona

                    Date date = new Date();
                    currentBeacons.put(key, new Timestamp(date.getTime())); //dodaje do listy wraz z timestampem
                    macBeacons.put(key, new ArrayList<RowBean>());
                    String distance = getDistance(beacon);
                    distanceBeacons.remove(key);
                    distanceBeacons.put(key, distance);
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ourRegion); //nasluchuje
                } catch (Exception rException) {
                    rException.printStackTrace();
                }
            }
        });
    }

    //czyszcze liste beaconow (zeby nie zostawaly "smieci")
    private void cleanOldBeaconsAfter(final Double timeInHours) {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (threadsShouldBeRunning) {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Date date = new Date();
                    Timestamp twoHoursAgo = new Timestamp(date.getTime() - (long) (time * MINUTE));
                    try {
                        for (Map.Entry<String, Timestamp> entry : currentBeacons.entrySet()) { //jesli przez ostatnie 2 h nie pojawil sie beacon z danym identyfikatorem - usuwam go z listy
                            String key = entry.getKey();
                            Timestamp timestamp = entry.getValue();
                            if (timestamp.before(twoHoursAgo)) {
                                currentBeacons.remove(key); //usuwam
                                macBeacons.remove(key);

                            }
                        }
                    }catch (Exception e){e.printStackTrace(); }

                }
            }
        };

        t.start();
    }

   /* public void addUser() {
        address = SERVER_ADDRESS + "/api/user/add";

        Map<String, String> data = new HashMap<String, String>();
        data.put(tag_mac, currentBeacons.toString());
        Log.d("send beacon", data.toString());
        JSONObject json = new JSONObject(data);

        send(address, json);
    }

    private void userList() { //wysylam liste do serwera
        address = SERVER_ADDRESS + "/api/user/list";

        Map<String, String> data = new HashMap<String, String>();
        data.put(tag_mac, currentBeacons.toString());

        JSONObject json = new JSONObject(data);

        send(address, json);
    }

    private void addBeacon(String beaconID, String event, String description) {
        address = SERVER_ADDRESS + "/api/beacon/add";

        Map<String, String> data = new HashMap<String, String>();
        data.put(tag_mac, beaconID);
        data.put(tag_description, description);
        data.put(tag_event, event);

        JSONObject json = new JSONObject(data);

        send(address, json);
    }

    private void send(String address, JSONObject data) {
        this.address = address;
        this.data = data;
        SendPostRequestTask sendTask = new SendPostRequestTask(this);
        sendTask.execute();
    }
    */

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public JSONObject getData() {
        return this.data;
    }

    @Override
    public int getDataLength() {
        return n;
    }

    @Override
    public void handleResponse(JSONObject response) {
    }


    /*

    public void handleListResponse(JSONArray data) {
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject friend = data.getJSONObject(i);
                Log.d("Friend found", friend.toString());
                updateFriendList(friend);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("friends", friends.toString());
        updateView();
    }

    private void updateFriendList(JSONObject rawFriend) throws JSONException {
        final String mac = (String) rawFriend.get(tag_mac);
        final String time = (String) rawFriend.get(tag_time);
        final String user = (String) rawFriend.get(tag_user);
        final String id = (String) rawFriend.get(tag_id);
        if (friends.get(mac) == null) {
            Log.d("Creating new known ", "beacon for: " + mac);
            friends.put(mac, new ArrayList<RowBean>());
        }

        List<RowBean> beacon = friends.get(mac);
        RowBean friend = new RowBean(user, id, null);
        friend.requestForGlobalId();
        beacon.add(friend);
    } */


    //ta funkcja pozwala na wyswietlanie beaconow w panelu admina
    private void updateView(){

        List<RowBean> allBeaconsInRange = new ArrayList<RowBean>();
        List<RowBean> allBeaconsInRangeDistance = new ArrayList<RowBean>();
        for(String mac: macBeacons.keySet()) {
                //mam wszystkie dostepne mac_add
                if (!allBeaconsInRange.contains(new RowBean(mac))) {
                    allBeaconsInRange.add(new RowBean(mac));
                    allBeaconsInRangeDistance.add(new RowBean("distance: " + String.valueOf(distanceBeacons.get(mac)))); //jesli nie bylo takiego wczesniej - dodaje do listy
                }
        }
        RowBean[] data = new RowBean[allBeaconsInRangeDistance.size()];
        data = allBeaconsInRangeDistance.toArray(data); //tworze tabele z dostepnych beaconow

        RowAdapter adapter = new RowAdapter(this, R.layout.beacon_item, data); //ustawiam adapter, po prostu mam widok listy
        listView1.setAdapter(adapter);
        n=allBeaconsInRange.size();
        int k = getDataLength();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("BEACON_COUNT", k); //InputString: from the EditText
        editor.apply();
    }


    public void startRadar(View view){
        Intent i = new Intent (this, RadarActivity.class);
        startActivity(i);
    }

}
