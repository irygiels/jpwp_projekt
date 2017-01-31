package cisco.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by irygiels on 16.04.16.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db";

    //obsluga db.

    private static Database sInstance;

    public static synchronized Database getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new Database(context.getApplicationContext());
        }
        return sInstance;
    }

    private Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL statements to create tables
        String CREATE_BEACON_TABLE = "CREATE TABLE Beacon ( " +
                "idBeacon INTEGER, " +
                "distance INTEGER, "+
                "Beacon_mac_id TEXT )";

        db.execSQL(CREATE_BEACON_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS Beacon");

        // create fresh tables
        this.onCreate(db);
    }


    public void insertData(float distance, String mac){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("distance", distance);
        values.put("Beacon_mac_id", mac);

        db.insert("BEACON", null, values);

    }


    //Cursor dla każdej tabeli - zapytanie SELECT*FROM, argumentem jest nazwa tabeli

    public Cursor selectFrom(String table_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + table_name, null);
        return res;
    }


    public Cursor selectRaw(String rawQuery){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(rawQuery, null);
        return res;
    }

    public void insertRecord(String queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL(queryValues);
        database.close();
    }



    public String[] selectRawRetTable(String selectQuery){

        // String selectQuery = "SELECT  * FROM " + Table_Name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String[] data = null;
        if (cursor.moveToFirst()) {
            do {
                // get  the  data into array,or class variable
            } while (cursor.moveToNext());
        }
        //db.close();
        return data;
    }

    /*


        // close
        db.close();
    }

    public Sensor getSensor(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_SENSOR, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit


    }*/
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

}
