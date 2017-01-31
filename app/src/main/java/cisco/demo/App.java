package cisco.demo;

import android.app.Application;
import android.content.Context;

/**
 * Created by Iga on 2017-01-24.
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}