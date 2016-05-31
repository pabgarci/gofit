package es.pabgarci.gofit;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationsDBHandler admin;
        SQLiteDatabase db;
        Button buttonGo = (Button) findViewById(R.id.button_workout);
        if (buttonGo != null) {
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMap(v);
                }
            });
        }
        Button buttonList = (Button) findViewById(R.id.button_list);
        if (buttonList != null) {
            buttonList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToList(v);
                }
            });
        }
    }

    public void goToMap(View view){
        Intent intent = new Intent(this, TrackerActivity.class);
        startActivity(intent);
    }

    public void goToList(View view){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
}
