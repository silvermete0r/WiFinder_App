package com.example.wifinderapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class detailsActivity extends AppCompatActivity {

    //custom list
    ArrayList<DetailsItem> arrayListCustom;
    DetailsAdapter adapterCustom;
    ListView customListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Back to WiFi List Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Custom List
        customListView = (ListView) findViewById(R.id.detailsList);
        arrayListCustom = new ArrayList<DetailsItem>();
        adapterCustom = new DetailsAdapter(this, R.layout.wifi_details_item, arrayListCustom);
        customListView.setAdapter(adapterCustom);

        //Getting values of WiFi Properties
        Bundle bundle = getIntent().getExtras();

        //Initializing Items:
        DetailsItem item1 = new DetailsItem("SSID", bundle.getString("ssid"));
        DetailsItem item2 = new DetailsItem("Capabilities", bundle.getString("security"));
        DetailsItem item3 = new DetailsItem("RSSI", bundle.getString("level"));
        DetailsItem item4 = new DetailsItem("Frequency", bundle.getString("frequency"));
        DetailsItem item5 = new DetailsItem("Distance", bundle.getString("distance"));
        DetailsItem item6 = new DetailsItem("BSSID", bundle.getString("bssid"));
        //Adding Items Into List
        arrayListCustom.add(item1);
        arrayListCustom.add(item2);
        arrayListCustom.add(item3);
        arrayListCustom.add(item4);
        arrayListCustom.add(item5);
        arrayListCustom.add(item6);

        adapterCustom.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}