package com.example.wifinderapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class compassActivity extends AppCompatActivity{

    // Declaring variables
    int stop = 0;

    // Main WiFi Finder Configurations
    private StringBuilder sb = new StringBuilder();
    private TextView tv;
    private List<ScanResult> scanList;

    // Loop configurations for calling wifi scanner
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 1600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        tv = (TextView) findViewById(R.id.txtWifiNetworks);
        getWifiNetworksList();

        //Back to WiFi List Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Running a function every 1600 milliseconds
    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                //Running with Delay
                handler.postDelayed(runnable, delay);
                getWifiNetworksList(); //Call a function to scan visible wifi networks
                //Clear cache memory
                getCacheDir().delete();
            }
        }, delay);
        super.onResume();
    }

    //Pause a wifi scanning loop
    @Override
    protected void onPause() {
        //Clear cache memory
        getCacheDir().delete();
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

    private void getWifiNetworksList() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        final WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(new BroadcastReceiver() {

            @SuppressLint("UseValueOf")
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    //Getting Network Physical ID
                    Bundle bundle = getIntent().getExtras();
                    //Init string builder
                    sb = new StringBuilder();
                    //WiFi Manager Scan
                    scanList = wifiManager.getScanResults();
                    int scanRes = scanList.size();
                    //Audio Play Binding
                    AudioTrack tone = generateTone(440, 250);
                    for (int i = 0; i < scanList.size() && stop<=30; i++) {
                        scanRes--;
                        stop++;
                        if (scanList.get(scanRes).BSSID.equals(bundle.getString("bssid"))) {
                            sb.append("SSID: " + scanList.get(scanRes).SSID + " [" + scanList.get(scanRes).BSSID + "] ");
                            sb.append("\n");
                            sb.append("Frequency: " + scanList.get(scanRes).frequency);
                            sb.append("\n");
                            sb.append("Strength: " + scanList.get(scanRes).level);
                            double distance = calculateDistance(scanList.get(scanRes).level, scanList.get(scanRes).frequency);
                            sb.append("\n\n");
                            sb.append("Current distance to wifi: " + String.valueOf(distance) + "m");
                            sb.append("\n\n");
                            if (distance <= 2) {
                                tone.play();
                            } else if (distance <= 5) {
                                tone.play();
                                Thread.sleep(150);
                            } else if (distance <= 10) {
                                tone.play();
                                Thread.sleep(250);
                            } else if (distance <= 15) {
                                tone.play();
                                Thread.sleep(500);
                            } else if (distance <= 20) {
                                tone.play();
                                Thread.sleep(800);
                            } else if (distance <= 25) {
                                tone.play();
                                Thread.sleep(850);
                            } else if (distance <= 30) {
                                tone.play();
                                Thread.sleep(1000);
                            } else if (distance <= 40) {
                                tone.play();
                                Thread.sleep(1100);
                            } else if (distance <= 50) {
                                tone.play();
                                Thread.sleep(1200);
                            } else if (distance <= 70) {
                                tone.play();
                                Thread.sleep(1300);
                            } else if (distance <= 100) {
                                tone.play();
                                Thread.sleep(1400);
                            } else {
                                tone.play();
                                Thread.sleep(1500);
                            }
                            break;
                        } else {
                            continue;
                        }
                    }
                    tv.setText(sb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, filter);
        wifiManager.startScan();
    }

    // Calculating Distance to WiFi
    public static double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return (double) Math.round(Math.pow(10.0, exp) * 100d) / 100d;
    }

    //Sound Generating Class
    private AudioTrack generateTone(double freqHz, int durationMs)
    {
        int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];
        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
        track.write(samples, 0, count);
        return track;
    }

    //Back to home
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