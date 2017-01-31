package cisco.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class RegisterUser extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText confirm;
    HashMap<String, String> map =new HashMap<String, String>();
    String pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        username = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editPass);
        confirm = (EditText)findViewById(R.id.editLogin);

    }

    public void Register (View view) {
        map.put(String.valueOf(username.getText()), String.valueOf(password.getText()));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LOG", String.valueOf(username.getText()));
        editor.putString("PASS", String.valueOf(password.getText()));
        editor.apply();
        if(password.getText().toString().equals(confirm.getText().toString())){
        Toast.makeText(RegisterUser.this, "credentials of user: " + username.getText() + " added", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, LoadCredentials.class);
        startActivity(i);}
        else {
            Toast.makeText(RegisterUser.this, "passwords do not match!", Toast.LENGTH_LONG).show();

        }
    }


}
