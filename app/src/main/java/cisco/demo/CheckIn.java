package cisco.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by irygiels on 16.03.16.
 */
public class CheckIn extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    Set toSet;
    ArrayList<Set<String>> lista = new ArrayList<>();
    Set<String> set = new HashSet<String>();
    boolean zoneChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        textView = (TextView)findViewById(R.id.txt);
        imageView = (ImageView)findViewById(R.id.image);


        String whereAmI;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                whereAmI= null;
            } else {
                whereAmI= extras.getString("chosen");
            }
        } else {
            whereAmI= (String) savedInstanceState.getSerializable("chosen");
        }

        assert whereAmI != null;
        if(whereAmI.contains("starbucks")){
            imageView.setImageResource(R.drawable.starbucks);}
        else if(whereAmI.contains("kfc")){
            imageView.setImageResource(R.drawable.kfc);}
        else if(whereAmI.contains("burger")){
            imageView.setImageResource(R.drawable.burger);}


    }


    public void checkIn(View view) throws InterruptedException {
        if(textView.getText().toString().equals("CHECK IN! (3)")){
            lista = new ArrayList<>();
            lista.clear();
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading coordinates...");
            progress.show();

            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                    // You don't need anything here
                }

                public void onFinish() {
                    progress.dismiss();
                }
            }.start();
// To dismiss the dialog
            getDistanceSet(0);

            textView.setText("CHECK IN! (2)");}
        else if(textView.getText().toString().equals("CHECK IN! (2)")){
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading coordinates...");
            progress.show();

            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                    // You don't need anything here
                }

                public void onFinish() {
                    progress.dismiss();
                }
            }.start();
            getDistanceSet(1);

            textView.setText("CHECK IN! (1)");}
        else if(textView.getText().toString().equals("CHECK IN! (1)")){
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading coordinates...");
            progress.show();

            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                    // You don't need anything here
                }

                public void onFinish() {
                    progress.dismiss();
                }
            }.start();
            getDistanceSet(2);

            Toast.makeText(CheckIn.this, "Your zone has been successfully saved", Toast.LENGTH_LONG).show();
            Toast.makeText(CheckIn.this, lista.toString(), Toast.LENGTH_LONG).show();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            toSet = new HashSet(lista);
            editor.putStringSet("ZONE", toSet); //InputString: from the EditText
            editor.apply();
            //getCoordinates(lista);
            toSet.clear();
            Intent i = new Intent(this, BeaconFinder.class);
            startActivity(i);
        }
    }

    public void getDistanceSet(int i){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        set = prefs.getStringSet("SET", new HashSet<String>());
        TreeSet<String> treeSet = new TreeSet<String>(set);
        try{
            assert !treeSet.isEmpty();
            lista.add(i, treeSet);}
        catch (Exception e){ e.printStackTrace(); }
    }

}
