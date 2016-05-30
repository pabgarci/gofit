package es.pabgarci.gofit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/*
* With this class we manage the Database
* It creates the different columns we will use to store the locations.
* */


public class LocationsDBHandler extends SQLiteOpenHelper {

    public LocationsDBHandler(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Locations(_id integer primary key autoincrement, NAME text, LOCATION text, DISTANCE real, TIME int, DATE text);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int lastVersion, int newVersion) {
        db.execSQL("drop table if exists Locations");
        db.execSQL("create table Locations(_id integer primary key autoincrement, NAME text, LOCATION text, DISTANCE real, TIME int, DATE text);");
    }
}