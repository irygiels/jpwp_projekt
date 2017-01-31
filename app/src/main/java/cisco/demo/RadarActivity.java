package cisco.demo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import static java.lang.Thread.sleep;

public class RadarActivity extends AppCompatActivity {
    int n;
    int k;
    String p;
    String user;
    boolean stan;
    boolean stanBefore;
    boolean notOpened;
    int nr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                user= null;
            } else {
                user= extras.getString("username");
            }
        } else {
            user= (String) savedInstanceState.getSerializable("username");
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        nr=0;
        k = prefs.getInt("BEACON_COUNT", 0);
        n = prefs.getInt("NUMBER_I_NEED", 0); //no id: default value
        setContentView(R.layout.activity_radar);
        if(user!=null){
        setTitle("You are currently logged as: "+ user);}
        else{setTitle("You are currently logged as: ADMIN");}

        final RandomTextView randomTextView = (RandomTextView) findViewById(
                R.id.random_textview);

        randomTextView.setOnRippleViewClickListener(
                new RandomTextView.OnRippleViewClickListener() {
                    @Override
                    public void onRippleViewClicked(View view) {
                        RadarActivity.this.startActivity(
                                new Intent(RadarActivity.this, BeaconFinder.class));
                    }
                });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    randomTextView.removeAllViewsInLayout();
                    if (k != 0) {
                        for (int i = 0; i < k; i++) {
                            randomTextView.addKeyWord("beacon no. " + String.valueOf(i+1));
                            randomTextView.show();
                        }
                    } else {
                        for (int i = 0; i < n; i++) {
                            randomTextView.addKeyWord("beacon no. " + String.valueOf(i+1));
                            randomTextView.show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2 * 1000);

        isInZone();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_radar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void isInZone(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                refreshState();
                            }
                        });
                        Thread.sleep(5000);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    private void refreshState(){
        stanBefore = stan;
        stan = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("INZONE", false);
        p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("PROX", "");
        Log.d("DANE DANE", p);
        if(stan && !stanBefore) {
            notOpened=true;
            if (user != null) {
                Toast.makeText(RadarActivity.this, "HI " + user + "!", Toast.LENGTH_SHORT).show();
                if (stan) {
                    Toast.makeText(RadarActivity.this, "You are currently in the zone!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RadarActivity.this, "You are currently out of zone!", Toast.LENGTH_SHORT).show();
                }
                // initialize a credentials provider object with your Activity’s context and
// the values from your identity pool
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(), // get the context for the current activity
                        "4019-8523-3797", // your AWS Account id
                        "us-east-1_SLmd5bSoJ", // your identity pool id
                        "arn:aws:iam::401985233797:role/Cognito_AmrestAuth_Role",// an authenticated role ARN
                        "arn:aws:iam::401985233797:role/Cognito_AmrestUnauth_Role", // an unauthenticated role ARN
                        Regions.US_EAST_1 //Region
                );


                AmazonDynamoDBClient ddb = new AmazonDynamoDBClient(credentialsProvider);
                ddb.setRegion(Region.getRegion(Regions.US_EAST_1));

                CognitoSyncManager syncClient = new CognitoSyncManager(
                        getApplicationContext(),
                        Regions.US_EAST_1,
                        credentialsProvider
                );

                Dataset dataset = syncClient.openOrCreateDataset("amrestUsers");
                dataset.put("id", user);
                dataset.put("zone", String.valueOf(1));
            }
            else {
                Toast.makeText(RadarActivity.this, "HI ADMIN!", Toast.LENGTH_SHORT).show();
            }
        }
            else if(p.contains("IMMEDIATE") && nr%5==0) {

                nr++;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i= new Intent(RadarActivity.this, Jpeg.class);
                        startActivity(i);

                        // close this activity
                        finish();
                    }
                }, 3000);
            notOpened=false;
        }

        }

    }


