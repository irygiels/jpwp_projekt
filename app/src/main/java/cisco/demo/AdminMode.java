package cisco.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AdminMode extends Activity implements OnItemClickListener {

    private ListView listViewRestaurant;
    private Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mode);
        ctx=this;
        //tutaj trzeba bedzie nastepnie zmienic na pobranie danych z serwera - proponuje json i od serwera w jednym jsonie te 3 wartosci
        List listRestaurant= new ArrayList();
        listRestaurant.add(new Restaurant("Starbucks","Aleje Jerozolimskie 63","starbucks"));
        listRestaurant.add(new Restaurant("KFC","Piękna 28/34","kfc"));
        listRestaurant.add(new Restaurant("Burger King","Złota 59","burger"));

        listViewRestaurant = ( ListView ) findViewById(R.id.restaurant_list);
        listViewRestaurant.setAdapter(new RestaurantAdapter(ctx, R.layout.restaurant_custom_view, listRestaurant));
        listViewRestaurant.setOnItemClickListener(this);
        //listViewRestaurant.setItemsCanFocus(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView)view.findViewById(R.id.resturantName);
        Intent i = new Intent(this, CheckIn.class);
        i.putExtra("chosen", textView.getText().toString().toLowerCase());
        Log.d("chosen log", textView.getText().toString().toLowerCase());
        startActivity(i);
    }
}