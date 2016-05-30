package es.pabgarci.gofit;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ListActivityOld extends AppCompatActivity {

    LocationsDBHandler admin;
    SQLiteDatabase db;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        admin = new LocationsDBHandler(this, "Locations", null, 1);
        db = admin.getWritableDatabase();
        list = (ListView) findViewById(R.id.listView);
        setListView();
        Toast.makeText(getApplicationContext(), ""+countDB(),Toast.LENGTH_LONG).show();
    }

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
            String location;
            Double distance;
            Double time;
            String date;
            String text;
            int id;

            db = admin.getWritableDatabase();

            Cursor c = db.rawQuery("SELECT _id, NAME, LOCATION, DISTANCE, TIME, DATE FROM Locations WHERE _id=" + i, null);
            c.moveToFirst();
            id = c.getInt(0);
            name = c.getString(1);
            location = c.getString(2);
            distance = c.getDouble(3);
            time = c.getDouble(4);
            date = c.getString(5);
            c.close();
            text = id + ".- " + name + ", " + location + ", " + distance +", "+ time +", "+ date;

            values[i - 1] = text;
        }


        return values;
    }

    public void goToLocationDetails(int id) {

        db = admin.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT _id, NAME, LOCATION, DISTANCE, TIME, DATE FROM Locations WHERE _id=" + id, null);
        c.moveToFirst();

        Intent intent = new Intent(this, LocationDetailsActivity.class);
        Bundle b = new Bundle();
        b.putString("SHOWNAME", c.getString(1));
        b.putString("SHOWLOCATION", c.getString(2));
        b.putDouble("SHOWDISTANCE", c.getDouble(3));
        b.putDouble("SHOWTIME",c.getDouble(4));
        b.putString("SHOWDATE", c.getString(5));
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

    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }



}
