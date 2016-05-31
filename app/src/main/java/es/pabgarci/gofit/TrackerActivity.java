package es.pabgarci.gofit;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.Manifest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TrackerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    final static double RUNNING6MPH = 10;
    final static double VALUE = 0.0175;
    final static double WEIGHT = 80;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    TextView textIzq1;
    TextView textIzq2;
    TextView textDer1;
    TextView textDer2;
    TextView textCent;
    Boolean STATE = false;
    Location lastLocation;
    float distance;
    static double calories = 0;
    Polyline polyline;
    Marker marker;
    Button buttonStartStop;
    Button buttonFinish;

    LocationsDBHandler admin;
    SQLiteDatabase db;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            long millis = elapsedTime + (System.currentTimeMillis() - startTime);

            textCent.setText(String.format("%2d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis)
            ));
            caloriesPerMinute(TimeUnit.MILLISECONDS.toMinutes(millis));
            handler.postDelayed(runnable, 100);
        }
    };

    Long startTime;
    Long elapsedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buttonStartStop = (Button) findViewById(R.id.button_start_stop);
        if (buttonStartStop != null) {
            buttonStartStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timeCounter(v);
                }
            });
        }
        buttonFinish = (Button) findViewById(R.id.button_finish);
        if (buttonFinish != null) {
            buttonFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
        }
        assert buttonFinish != null;
        buttonFinish.setVisibility(View.GONE);
        textIzq1 = (TextView) findViewById(R.id.izq1);
        textIzq2 = (TextView) findViewById(R.id.izq2);
        textDer1 = (TextView) findViewById(R.id.der1);
        textDer2 = (TextView) findViewById(R.id.der2);
        textCent = (TextView) findViewById(R.id.cent);

        admin = new LocationsDBHandler(this, "Locations", null, 1);
        db = admin.getWritableDatabase();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void moveMapToPosition(Location location, Location lastLocationAux) {
        if(marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        if(lastLocationAux != null && lastLocationAux != location && STATE) {
            polyline = mMap.addPolyline(new PolylineOptions().add(new LatLng(lastLocationAux.getLatitude(), lastLocationAux.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude())).width(7).color(Color.BLUE));
        }
    }

    public void caloriesPerMinute(long time) {
        Long timeAux = time;
        calories =  VALUE * RUNNING6MPH * WEIGHT * timeAux.doubleValue() ;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGoogleApiClient.connect();
    }

    public void onLocationChanged(Location location) {
        if(lastLocation != null && STATE) {
            distance = distance + lastLocation.distanceTo(location);
        }
        moveMapToPosition(location, lastLocation);
        lastLocation = location;
        textIzq1.setText(String.format("%.2s km/h", String.valueOf(3.6 * location.getSpeed())));
        textIzq2.setText(String.format("%sm", String.valueOf(location.getAccuracy())));
        textDer1.setText(String.format("%.0f cal", calories));
        textDer2.setText(String.format("%.2f km", (distance * .001)));
    }

    public void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activity name");
        elapsedTime = elapsedTime + (System.currentTimeMillis() - startTime);
        handler.removeCallbacks(runnable);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveData(input.getText().toString(), elapsedTime);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public String getAddress(double myLat, double myLng){
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        String textAddress = "";

        try {
            List<Address> myAddress = geoCoder.getFromLocation(myLat,myLng,1);
            if (myAddress.size() > 0) {
                for (int i = 0; i < myAddress.get(0).getMaxAddressLineIndex(); i++)
                    textAddress = myAddress.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return textAddress;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("MyLocation", "Connection to Google Api has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult cResult) {
        Log.i("MyLocation", "Connection to Google Api has failed");

    }

    @Override
    public void onConnected(Bundle bundle) {

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void saveData(String name, Long time){
        if(!STATE){
            Calendar c = Calendar.getInstance();
            long millis = elapsedTime + (System.currentTimeMillis() - startTime);
            writeDB(name, getAddress(lastLocation.getLatitude(),lastLocation.getLongitude()),distance * .001, sdf.format(c.getTime()), String.format("%2d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis)
            ), calories);
        }
    }

    public void timeCounter(View v) {
        if (!STATE) {
            STATE=true;
            startTime = System.currentTimeMillis();
            buttonStartStop.setText("Pause");
            buttonFinish.setVisibility(View.GONE);
            handler.postDelayed(runnable, 100);
            runnable.run();
        }else if(STATE){
            STATE=false;
            buttonStartStop.setText("Continue");
            buttonFinish.setVisibility(View.VISIBLE);
            elapsedTime = elapsedTime + (System.currentTimeMillis() - startTime);
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    finish();
                    startActivity(getIntent());
                }
            }
        }
    }

    public int countDB() {
        int count;
        Cursor c = db.rawQuery("Select * from Locations", null);
        count = c.getCount();
        c.close();
        return count;
    }

    public void writeDB(String name, String location, Double distance, String date, String time, Double kcal) {
        ContentValues registro = new ContentValues();  //es una clase para guardar datos
        registro.put("_id", countDB() + 1);
        registro.put("NAME", name);
        registro.put("LOCATION", location);
        registro.put("DISTANCE", distance);
        registro.put("DATE", date);
        registro.put("TIME", time);
        registro.put("KCAL",kcal);
        db.insert("Locations", null, registro);
    }

}
