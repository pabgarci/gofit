package es.pabgarci.gofit;

import android.app.ExpandableListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends ExpandableListActivity {

    LocationsDBHandler admin;
    SQLiteDatabase db;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        admin = new LocationsDBHandler(this, "Locations", null, 1);
        db = admin.getWritableDatabase();
        SimpleExpandableListAdapter expListAdapter =
                new SimpleExpandableListAdapter(
                        this,
                        createGroupList(),              // Creating group List.
                        R.layout.group_row,             // Group item layout XML.
                        new String[] { "Group Item" },  // the key of group item.
                        new int[] { R.id.row_name },    // ID of each group item.-Data under the key goes into this TextView.
                        createChildList(),              // childData describes second-level entries.
                        R.layout.child_row,             // Layout for sub-level entries(second level).
                        new String[] {"Sub Item"},      // Keys in childData maps to display.
                        new int[] { R.id.grp_child}     // Data under the keys above go into these TextViews.
                );
        setListAdapter( expListAdapter );
    }

    //returns the number of rows of the database
    public int countDB() {
        int count;
        Cursor c = db.rawQuery("Select * from Locations", null);
        count = c.getCount();
        c.close();
        return count;
    }

    @SuppressWarnings("unchecked")
    private List createGroupList() {
        ArrayList result = new ArrayList();
        for( int i = 0 ; i < 9 ; i++ ) {
            HashMap m = new HashMap();
            int z = i+1;
            db = admin.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT NAME FROM Locations WHERE _id=" + z, null);
            c.moveToFirst();
            m.put( "Group Item",c.getString(0));
            result.add( m );
        }
        return result;
    }

    public void  onContentChanged  () {
        System.out.println("onContentChanged");
        super.onContentChanged();
    }
    /* This function is called on each child click */
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        System.out.println("Inside onChildClick at groupPosition = " + groupPosition +" Child clicked at position " + childPosition);
        return true;
    }

    /* This function is called on expansion of the group */
    public void  onGroupExpand  (int groupPosition) {
        try{
            System.out.println("Group exapanding Listener => groupPosition = " + groupPosition);
        }catch(Exception e){
            System.out.println(" groupPosition Errrr +++ " + e.getMessage());
        }
    }

    public String getContent(int n){
        db = admin.getWritableDatabase();
        int z = n+1;
        Cursor c = db.rawQuery("SELECT _id, NAME, LOCATION, DISTANCE, TIME, DATE FROM Locations WHERE _id=" + z, null);
        c.moveToFirst();
        String aux = "";
        switch (n){
            case 0:
                aux = "Date: "+c.getString(5);
                break;
            case 1:
                aux = "Location: "+c.getString(2);
                break;
            case 2:
                aux = "Distance: "+c.getString(3);
                break;
            case 3:
                aux = "Time: "+c.getString(4);
                break;

        }
        return aux;
    }

    @SuppressWarnings("unchecked")
    private List createChildList() {

        ArrayList result = new ArrayList();
        for( int i = 0 ; i < 9 ; ++i ) {
            ArrayList secList = new ArrayList();
            for( int n = 0 ; n < 4 ; n++ ) {
                HashMap child = new HashMap();
                child.put( "Sub Item", getContent(n) );
                secList.add( child );
            }
            result.add( secList );
        }
        return result;
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
