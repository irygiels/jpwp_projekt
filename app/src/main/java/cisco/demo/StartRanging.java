package cisco.demo;

import android.app.ListActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.rtp.AudioStream;

import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;

import static java.lang.Thread.sleep;

/**
 * Created by irygiels on 29.10.15.
 */
public class StartRanging {


    BeaconManager beaconManager;
    int m;
    Region ourRegion;
    private final Double time = 0.05;
    private final long MINUTE = 60*1000;
    private volatile boolean threadsShouldBeRunning = true; //czy wszystko ok - boolean
    private Map<String, Timestamp> currentBeacons;
    private Map<String, List<RowBean>> macBeacons;
    Set<String> zone = new HashSet<String>();
    ArrayList zoneList;
    Context cont;
    boolean inZone;
    private Map<String, String> distanceBeacons;
    private Map<String, String> maxBeacons;
    private Map<String, String> minBeacons;
    String proximity = "";

    public StartRanging(Context context) {
        cont=context;
        m=0;
        currentBeacons = new HashMap<String, Timestamp>();
        macBeacons = new HashMap<String, List<RowBean>>();
        distanceBeacons = new HashMap<String, String>();
        beaconManager = new BeaconManager(cont);
        beaconManager.setForegroundScanPeriod(1000, 2700);
        ourRegion = new Region("region", null, null, null);
        startRangingBeacons(cont);
        startSendingKnownBeaconsToServer();
        //connectBeacons();
    }

    public void startRangingBeacons(final Context context) {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                for (Beacon beacon : beacons) {
                    proximity=String.valueOf(getProximity(beacon));
                    String key = makeKey(beacon); //identyfikator beacona

                    Date date = new Date();
                    currentBeacons.put(key, new Timestamp(date.getTime())); //dodaje do listy wraz z timestampem
                    macBeacons.put(key, new ArrayList<RowBean>());
                    String distance = getDistance(beacon);
                    distanceBeacons.remove(key);
                    distanceBeacons.put(key, distance);
/*                    if(!minBeacons.containsKey(key)){
                        minBeacons.put(key, "0");}
                    if(!maxBeacons.containsKey(key)){
                        maxBeacons.put(key, "0");}*/

                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    try {
                        beaconManager.startRanging(ourRegion);
                    } catch (Exception rException) {
                        rException.printStackTrace();
                    }
                }
            });
    }

    private String getDistance(Beacon beacon) {
        double distance = Math.min(Utils.computeAccuracy(beacon), 10.0); //do 10 m
        String wyn = String.format("%.2f", distance);
        return wyn;
    }

   /* private Boolean inZone(String mac) {
        getDistance(beacon);

        return isInZone;
    }*/

    private double maxDistance(Beacon beacon){
        double dis = 0.0;
        return dis;
    }

    private double minDistance(Beacon beacon){
        double dis = 0.0;
        return dis;
    }

    public com.estimote.sdk.Utils.Proximity getProximity(Beacon beacon) {
        return Utils.proximityFromAccuracy(Double.valueOf(getDistance(beacon)));
    }

    //tutaj na podstawie wlasnosci beacona (major+minor) tworze jego identyfikator
    private String makeKey(Beacon beacon) {
        //
        //final String key = Integer.toString(beacon.getMajor()) + Integer.toString(beacon.getMinor());
        final String key = String.valueOf(beacon.getMacAddress());
        return key;
    }


    private void startSendingKnownBeaconsToServer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (threadsShouldBeRunning) {
                    getZone();
                    //co sie dzieje w tle
                    try {
                        sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }



    public void getZone(){
        zoneList = new ArrayList<String>();
        zoneList.clear();
        maxBeacons = new HashMap<String, String>();
        minBeacons = new HashMap<String, String>();
        Set<String> set = new HashSet<String>();
        List<String> allBeaconsInRange = new ArrayList<>();
        List<String> allBeaconsInRangeSend = new ArrayList<>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cont);
        SharedPreferences.Editor editor = prefs.edit();

        zone = prefs.getStringSet("ZONE", new HashSet<String>()); //strefa wpisana w checkinie
        //otrzymuje ja jako SET<String>
        //chce ja teraz przerzucic do hashmapy

      zoneList.add(zone);
      getCoordinates(zoneList);


        Log.d("MOJE DANE", minBeacons.keySet().toString() + " " + maxBeacons.keySet().toString());
        for(String mac: macBeacons.keySet()) {
            if (!allBeaconsInRange.contains(mac)) {
                allBeaconsInRange.add(mac);
                allBeaconsInRangeSend.add(mac + " " + String.valueOf(distanceBeacons.get(mac)));
                m++;
                try{
                    if (Double.valueOf(distanceBeacons.get(mac).replace(",", ".")) < Double.valueOf(maxBeacons.get(mac).replace(",", "."))+0.4
                            && Double.valueOf(distanceBeacons.get(mac).replace(",", ".")) > Double.valueOf(minBeacons.get(mac).replace(",", "."))-0.2 || m%5==0) {
                        inZone = true;
                    }
                }catch (Exception e){ e.printStackTrace(); }
                set.add(String.valueOf(mac + " " + distanceBeacons.get(mac)));
            }
        }
        Log.d("JAKIES DANE", String.valueOf(inZone));

        int n = allBeaconsInRange.size();
        editor.putBoolean("INZONE", inZone);
        inZone = false;
        editor.putInt("NUMBER_I_NEED", n);
        editor.putStringSet("SET", set);
        editor.putString("PROX", proximity);
        Log.d("DANE", proximity);
        editor.putInt("SIZE", allBeaconsInRange.size());
        editor.putString("LISTA", allBeaconsInRangeSend.toString());

        editor.apply();
    }


    //ponizej funkcja, ktorej nie uzywam, aleeee - moze sie przydac jeszcze pozniej
    //zmienia liste na koordynaty oddzielnie

    public void getCoordinates(ArrayList<Set<String>> lista){
        ArrayList<String> records = new ArrayList<>();
        ArrayList<String> results = new ArrayList<>();
        for(int j = 0; j<lista.size(); j++){
            records.add(Arrays.toString(lista.get(j).toString().split(" ,"))); //biore wers
            String[] str = records.get(j).split(", ");
            for(int k = 0; k<str.length; k++){
                if(!str[k].equals(""))    {
                    String s = str[k].replaceAll("[^:,\\w\\s]","");
                    String[] addToMap = s.split(" ");
                    String myKey="";
                    String myValue="";
                    try{
                        myKey = "["+addToMap[0]+"]";
                        myValue = addToMap[1];}
                    catch (Exception e){e.printStackTrace();}
                    //results.add(s);
                    if(!myKey.equals("") && !minBeacons.containsKey(myKey) || (minBeacons.containsKey(myKey)
                            && Double.valueOf(minBeacons.get(myKey).replace(",", ".")) > Double.valueOf(myValue.replace(",", ".")))){
                        if(minBeacons.containsKey(myKey)){ minBeacons.remove(myKey); }
                        minBeacons.put(myKey, myValue);}
                    if(!myKey.equals("") && !maxBeacons.containsKey(myKey) || (maxBeacons.containsKey(myKey)
                            && Double.valueOf(maxBeacons.get(myKey).replace(",", ".")) < Double.valueOf(myValue.replace(",", ".")))){
                        if(maxBeacons.containsKey(myKey)){
                            maxBeacons.remove(myKey); }
                        maxBeacons.put(myKey, myValue);}
                    //Log.d("RESULTS", results.get(results.size() - 1));
                }
            }

        }

    }



}
