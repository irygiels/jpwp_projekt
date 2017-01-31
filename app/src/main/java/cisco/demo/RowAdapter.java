package cisco.demo;

/**
 * Created by irygiels on 01.03.16.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Thread.sleep;

public class RowAdapter extends ArrayAdapter<RowBean> {

    Context context;
    int layoutResourceId;
    RowBean data[] = null;

    public RowAdapter(Context context, int layoutResourceId, RowBean[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        Log.d("adapter", "constructor called");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RowBeanHolder holder = null;
        final RowAdapter that = this;
        final RowBean object = data[position];
        Log.d("adapter", "is called");
//        Log.d("adapter_row", object.id);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RowBeanHolder();

            holder.UserName = (TextView)row.findViewById(R.id.mac_add);
            holder.UserName.setText(object.getUserName());

        }
        else
        {
            holder = (RowBeanHolder)row.getTag();
        }


//        holder.UserName.setText(object.userName);
//        Log.d("holder", object.userName);

        return row;
    }




    public static class RowBeanHolder
    {

        TextView UserName;

    }


}