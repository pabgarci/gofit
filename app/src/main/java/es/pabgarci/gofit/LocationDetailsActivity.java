package es.pabgarci.gofit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;

/*
* This activity shows the details of a saved location from the database
* it shows the picture saved from the location, the street, city and country,
* as well as the latitude and longitude of the location.
* To show the picture first it gets the size of the window to determine the size of the picture
 */


public class LocationDetailsActivity extends AppCompatActivity {

    double showLat;
    double showLon;


    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    /*
 *IMPORTANT NOTE: we couldn't manage to make it work in certain phone brands,
 * because of their own camera app. The native camera app saves the picture in its default folder
 * and with the default name, this way when we want to get the picture we have a null pointer exception
 * in the name and folder we are using, but in some brands such as samsung it works perfectly
 * */
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        Bundle b = getIntent().getExtras();
        String showName = b.getString("SHOWNAME");
        String showLocation = b.getString("SHOWLOCATION");
        String showDistance = b.getString("SHOWDISTANCE");
        String showDate = b.getString("SHOWDATE");
        String showTime = b.getString("SHOWTIME");
        showLat = b.getDouble("SHOWLAT");
        showLon = b.getDouble("SHOWLON");

        TextView detailsName = (TextView)findViewById(R.id.textView_details_name);
        TextView detailsLocation = (TextView)findViewById(R.id.textView_details_location);
        TextView detailsDistance = (TextView)findViewById(R.id.textView_details_distance);
        TextView detailsDate = (TextView)findViewById(R.id.textView_details_date);
        TextView detailsTime = (TextView)findViewById(R.id.textView_details_time);

        detailsName.setText(showName);
        detailsLocation.setText(showLocation);
        detailsDistance.setText(showDistance);
        detailsDate.setText(showDate);
        detailsTime.setText(showTime);
    }


}
