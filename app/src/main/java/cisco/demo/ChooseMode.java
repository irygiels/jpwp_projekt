package cisco.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class ChooseMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

    }

    public void Admin(View view){
        Intent i = new Intent(this, AdminMode.class);
        startActivity(i);
    }

    public void User(View view){
        Intent i = new Intent(this, LoadCredentials.class);
        startActivity(i);
    }

}
