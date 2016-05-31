package es.pabgarci.gofit;

import android.app.ExpandableListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    LocationsDBHandler admin;
    SQLiteDatabase db;
    ExpandableListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        list = (ExpandableListView)findViewById(R.id.list);
        admin = new LocationsDBHandler(this, "Locations", null, 1);
        db = admin.getWritableDatabase();
        Log.d("HOLA", ""+countDB());
        if(countDB()!=0) {
            SimpleExpandableListAdapter expListAdapter =
                    new SimpleExpandableListAdapter(
                            this,
                            createGroupList(),
                            R.layout.group_row,
                            new String[]{"Group Item"},
                            new int[]{R.id.row_name},
                            createChildList(),
                            R.layout.child_row,
                            new String[]{"Sub Item"},
                            new int[]{R.id.grp_child}
                    );
            list.setAdapter(expListAdapter);
        }
    }

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
        for( int i = 0 ; i < countDB() ; i++ ) {
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

    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        System.out.println("Inside onChildClick at groupPosition = " + groupPosition +" Child clicked at position " + childPosition);
        return true;
    }

    public void  onGroupExpand  (int groupPosition) {
        try{
            System.out.println("Group exapanding Listener => groupPosition = " + groupPosition);
        }catch(Exception e){
            System.out.println(" groupPosition Errrr +++ " + e.getMessage());
        }
    }

    public String getContent(int i, int n){
        db = admin.getWritableDatabase();
        int z = i+1;
        Cursor c = db.rawQuery("SELECT _id, NAME, LOCATION, DISTANCE, TIME, DATE, KCAL FROM Locations WHERE _id=" + z, null);
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
                aux = "Distance: "+String.format("%.2g%n km", c.getDouble(3));
                break;
            case 3:
                aux = "Time: "+c.getString(4);
                break;
            case 4:
                aux = "KCal: "+c.getDouble(5);
        }
        return aux;
    }

    @SuppressWarnings("unchecked")
    private List createChildList() {

        ArrayList result = new ArrayList();
        for( int i = 0 ; i < countDB() ; ++i ) {
            ArrayList secList = new ArrayList();
            for( int n = 0 ; n < 5 ; n++ ) {
                HashMap child = new HashMap();
                child.put( "Sub Item", getContent(i,n) );
                secList.add( child );
            }
            result.add( secList );
        }
        return result;
    }

    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }



}
