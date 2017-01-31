package cisco.demo;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by irygiels on 15.04.16.
 */
public class LoadCredentials extends AppCompatActivity {

    public EditText editLogin;
    public EditText editPass;
    String log = "";
    String pass = "";
    String k = "";
    String v = "";
    HashMap<String, String> mapa = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_credentials);
        editLogin = (EditText)findViewById(R.id.editLogin);
        editPass = (EditText)findViewById(R.id.editPass);

    }

    public void Login(View view){
        log = editLogin.getText().toString();
        pass = editPass.getText().toString();
        Log.d("PASS", pass);
        Log.d("PASS log", log);
        k = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("LOG", "");
        v = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("PASS", "");
        mapa.put(k,v);

        Log.d("PASS get", k);
        if (mapa.get(log).equals(pass)){

        //if(editLogin.getText().toString().equals("admin") || editLogin.getText().toString().equals("cisco")) {
            Intent i = new Intent(this, RadarActivity.class);
            i.putExtra("username", editLogin.getText().toString());
            Toast.makeText(LoadCredentials.this, "credentials of user: " + editLogin.getText().toString() + " sent to server", Toast.LENGTH_LONG).show();

            startActivity(i);
        }
        else{
            Toast.makeText(LoadCredentials.this, "user doesn't exist or password incorrect!", Toast.LENGTH_LONG).show();
        }
    }

    public void AddUser(View view){
        Intent i = new Intent(this, RegisterUser.class);
        startActivity(i);
    }

}
