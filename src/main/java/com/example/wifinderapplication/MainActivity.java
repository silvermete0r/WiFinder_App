package com.example.wifinderapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // constants
    private static final int PERMISSION_CODE = 1000;

    // variables
    int scanResultsSize = 0;
    int stop = 0;

    // UI components
    Button buttonScan;

    // loop configurations for calling wifi scanner
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 8000;

    // objects
    WifiManager wifiManager;
    List<ScanResult> scanResults;

    int[] images ={R.drawable.signal_level_1, R.drawable.signal_level_2, R.drawable.signal_level_3, R.drawable.signal_level_4};

    //custom list
    ArrayList<MyItem>arrayListCustom = new ArrayList<>();
    CustomAdapter adapterCustom;
    ListView customListView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind UI components
        buttonScan = (Button) findViewById(R.id.scanButton);
        buttonScan.setOnClickListener(this);

        // check for permissions
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permission, PERMISSION_CODE);
        } else {
            startWifiManager();
        }

        //Custom List
        customListView = (ListView) findViewById(R.id.wifiList);
        arrayListCustom = new ArrayList<MyItem>();
        adapterCustom = new CustomAdapter(this, R.layout.rowindex_item, arrayListCustom);
        customListView.setAdapter(adapterCustom);

        //Binding item click modes
        customListView.setOnItemClickListener(new MyItemClicked());
        customListView.setOnItemLongClickListener(new MyItemLongClicked());
    }

    // handle runtime permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                startWifiManager();
            } else {
                Toast.makeText(this, "Permission denied. Cannot run app.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // handle default WifiManager
    public void startWifiManager() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "WiFi is disabled.. Please Make it enabled!", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        scanWifiNetworks();
    }

    //Running a function every 8 seconds
    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                scanWifiNetworks(); //Call a function to scan visible wifi networks
            }
        }, delay);
        super.onResume();
    }

    //Pause a wifi scanning loop
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

    @Override
    public void onClick(View view) {
        scanWifiNetworks();
        Toast.makeText(this, "Available Networks Scanning...", Toast.LENGTH_SHORT).show();
    }

    // register BoaadcastReceiver
    private void scanWifiNetworks(){
        arrayListCustom.clear();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(wifiReceiver, intentFilter);

        wifiManager.startScan();
        Log.d("WifScanner", "Scanning Wifi Networks");
    }

    // Calculating Distance to WiFi
    public static double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return (double) Math.round(Math.pow(10.0, exp) * 100d) / 100d;
    }

    // BroadcastReceiver class
    private final BroadcastReceiver wifiReceiver= new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context c, Intent intent) {
            scanResults = wifiManager.getScanResults();
            scanResultsSize = scanResults.size();
            unregisterReceiver(this);
            for(ScanResult result : scanResults) {
                try {
                    while (scanResultsSize >= 0 && stop<=30) {
                        scanResultsSize--;
                        DecimalFormat df = new DecimalFormat("###.#");
                        double distance = calculateDistance(scanResults.get(scanResultsSize).level, scanResults.get(scanResultsSize).frequency);
                        int sLevel;
                        int sStrenth = scanResults.get(scanResultsSize).level;
                        if (sStrenth >= (-50)){
                            sLevel = 3;
                        } else if (sStrenth >= (-65)) {
                            sLevel = 2;
                        } else if (sStrenth >= (-80)) {
                            sLevel = 1;
                        } else {
                            sLevel = 0;
                        }
                        //Adding New Custom Item
                        MyItem newItem = new MyItem(scanResults.get(scanResultsSize).SSID, scanResults.get(scanResultsSize).capabilities, String.valueOf(scanResults.get(scanResultsSize).level) + " dBm", String.valueOf(scanResults.get(scanResultsSize).frequency) + " MHz", String.valueOf(df.format(distance)) + " m", scanResults.get(scanResultsSize).BSSID, images[sLevel]);
                        arrayListCustom.add(newItem);
                        adapterCustom.notifyDataSetChanged();
                        stop++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    class MyItemClicked implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MyItem list = arrayListCustom.get(position);
            Intent intent = new Intent(MainActivity.this,compassActivity.class);
            intent.putExtra("bssid", list.bssid);
            startActivity(intent);
        }
    }
    class MyItemLongClicked implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            MyItem list = arrayListCustom.get(position);
            Intent intent = new Intent(MainActivity.this,detailsActivity.class);
            intent.putExtra("ssid", list.ssid);
            intent.putExtra("security", list.security);
            intent.putExtra("level", list.level);
            intent.putExtra("frequency", list.frequency);
            intent.putExtra("distance", list.distance);
            intent.putExtra("bssid", list.bssid);
            startActivity(intent);
            return false;
        }
    }
}