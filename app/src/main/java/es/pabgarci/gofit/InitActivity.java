package es.pabgarci.gofit;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class InitActivity extends AppCompatActivity {

    LocationsDBHandler admin;
    SQLiteDatabase db;
    ListView list;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault());

    //returns the number of rows of the database
    public int countDB() {
        int count;
        Cursor c = db.rawQuery("Select * from Locations", null);
        count = c.getCount();
        c.close();
        return count;
    }


    public String[] fillArrayFromDB() {
        String values[] = new String[countDB()];

        for (int i = 1; i <= countDB(); i++) {


            String name;
            String time;
            String location;
            String text;
            Double distance;
            String date;
            int id;

            db = admin.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT _id, NAME, LOCATION, DISTANCE, TIME, DATE FROM Locations WHERE _id=" + i, null);
            c.moveToFirst();
            id = c.getInt(0);
            name = c.getString(1);
            location = c.getString(2);
            distance = c.getDouble(3);
            time = c.getString(4);
            date = sdf.format(c.getString(5)); //replace 4 with the column index
            c.close();
            text = id + ".- " + name + ", " + location + ", " + distance + time + date;

            values[i - 1] = text;
        }


        return values;
    }

    public String getName(int idAux) {

        String nameAux;

        db = admin.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT NAME FROM Locations WHERE _id=" + idAux, null);
        c.moveToFirst();
        nameAux = c.getString(0);
        c.close();
        return nameAux;

    }

    public double getLat(int idAux) {

        double latAux;

        db = admin.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT LAT FROM Locations WHERE _id=" + idAux, null);
        c.moveToFirst();
        latAux = c.getDouble(0);
        c.close();
        return latAux;

    }

    public double getLon(int idAux) {

        double lonAux;

        db = admin.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT LON FROM Locations WHERE _id=" + idAux, null);
        c.moveToFirst();
        lonAux = c.getDouble(0);
        c.close();

        return lonAux;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        admin = new LocationsDBHandler(this, "Locations", null, 1);
        db = admin.getWritableDatabase();
        list = (ListView) findViewById(R.id.listView);
        setListView();
    }

    public void goToLocationDetails(int id) {
        Intent intent = new Intent(this, LocationDetailsActivity.class);
        Bundle b = new Bundle();
        b.putString("SHOWNAME", getName(id));
        b.putDouble("SHOWLAT", getLat(id));
        b.putDouble("SHOWLON", getLon(id));
        intent.putExtras(b);
        startActivity(intent);

    }
    //Shows locations saved by the user in the listview
    public void setListView() {
        ArrayList<String> valuesList = new ArrayList<>();
        valuesList.addAll(Arrays.asList(fillArrayFromDB()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, valuesList);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                goToLocationDetails((int) id + 1);
                return true;
            }
        });

        list.setAdapter(adapter);
    }

    public void deleteDB() {
        db.delete("Locations", null, null);
        setListView();
    }

    public void writeDB(String name, String address, String city, double lat, double lon) {

        ContentValues registro = new ContentValues();  //es una clase para guardar datos
        registro.put("_id", countDB() + 1);
        registro.put("NAME", name);
        registro.put("ADDRESS", address);
        registro.put("CITY", city);
        registro.put("LAT", lat);
        registro.put("LON", lon);
        db.insert("Locations", null, registro);
        setListView();

    }

    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    //Recieves information from other intents and stores it in the database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        double showLat;
        double showLon;

        if (requestCode == 1) {

            if (data != null) {
                Bundle b = data.getExtras();
                String showName = b.getString("NAME");
                String showAddress = b.getString("ADDRESS");
                String showCity = b.getString("CITY");
                showLat = b.getDouble("LAT");
                showLon = b.getDouble("LON");
                String show = "Location saved" + "\n" + showName + "\n" + showAddress + ", " + showCity;
                Toast.makeText(getApplicationContext(), show, Toast.LENGTH_SHORT).show();
                writeDB(showName, showAddress, showCity, showLat, showLon);
                setListView();

            } else {
                Toast.makeText(getApplicationContext(), "No location", Toast.LENGTH_SHORT).show();
            }
        }
    }



}

