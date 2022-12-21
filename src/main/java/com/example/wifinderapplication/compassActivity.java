package com.example.wifinderapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class compassActivity extends AppCompatActivity implements SensorEventListener{

    @SuppressWarnings("unused")
    private String TAG = "SensorCompass";

    // Main View
    private RelativeLayout mFrame;

    // Sensors & SensorManager
    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager mSensorManager;

    // Storage for Sensor readings
    private float[] mGravity = null;
    private float[] mGeomagnetic = null;

    // Rotation around the Z axis
    private double mRotationInDegress;

    // View showing the compass arrow
    private CompassArrowView mCompassArrow;

    // Main WiFi Finder Configurations
    private StringBuilder sb = new StringBuilder();
    private StringBuilder db = new StringBuilder();
    private TextView tv, dv;
    private List<ScanResult> scanList;

    // Loop configurations for calling wifi scanner
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2023;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        tv = (TextView) findViewById(R.id.txtWifiNetworks);
        dv = (TextView) findViewById(R.id.distance);
        getWifiNetworksList();

        //Back to WiFi List Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFrame = (RelativeLayout) findViewById(R.id.activity_check_wifi_network);

        mCompassArrow = new CompassArrowView(getApplicationContext());

        mFrame.addView(mCompassArrow);

        // Get a reference to the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get a reference to the accelerometer
        accelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Get a reference to the magnetometer
        magnetometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Exit unless both sensors are available
        if (null == accelerometer || null == magnetometer)
            finish();
    }

    // Running a function every 2023 milliseconds
    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                //Running with Delay
                handler.postDelayed(runnable, delay);
                getWifiNetworksList(); //Call a function to scan visible wifi networks
                //Clear cache memory
                getCacheDir().delete();
                System.gc();
            }
        }, delay);
        // Register for sensor updates

        mSensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this, magnetometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Pause a wifi scanning loop
    @Override
    protected void onPause() {
        super.onPause();
        //Clear cache memory
        getCacheDir().delete();
        System.gc();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        // Unregister all sensors
        mSensorManager.unregisterListener(this);
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
                    db = new StringBuilder();
                    //WiFi Manager Scan
                    scanList = wifiManager.getScanResults();
                    int scanRes = scanList.size();
                    //Audio Play Binding
                    AudioTrack tone = generateTone(440, 250);
                    for (int i = 0; i < scanList.size(); i++) {
                        scanRes--;
                        if (scanList.get(scanRes).BSSID.equals(bundle.getString("bssid"))) {
                            sb.append("SSID: " + scanList.get(scanRes).SSID + " [" + scanList.get(scanRes).BSSID + "] ");
                            sb.append("\n");
                            sb.append("Frequency: " + scanList.get(scanRes).frequency);
                            sb.append("\n");
                            sb.append("Strength: " + scanList.get(scanRes).level);
                            double distance = calculateDistance(scanList.get(scanRes).level, scanList.get(scanRes).frequency);
                            sb.append("\n\n");
                            sb.append("Current distance to wifi: " + String.valueOf(distance) + "m");
                            db.append(String.valueOf(distance) + "m");
                            sb.append("\n\n");
                            if (distance <= 2) {
                                tone.play();
                                Thread.sleep(50);
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
                    dv.setText(db);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Acquire accelerometer event data

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            mGravity = new float[3];
            System.arraycopy(event.values, 0, mGravity, 0, 3);

        }

        // Acquire magnetometer event data

        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

            mGeomagnetic = new float[3];
            System.arraycopy(event.values, 0, mGeomagnetic, 0, 3);

        }

        // If we have readings from both sensors then
        // use the readings to compute the device's orientation
        // and then update the display.

        if (mGravity != null && mGeomagnetic != null) {

            float rotationMatrix[] = new float[9];

            // Users the accelerometer and magnetometer readings
            // to compute the device's rotation with respect to
            // a real world coordinate system

            boolean success = SensorManager.getRotationMatrix(rotationMatrix,
                    null, mGravity, mGeomagnetic);

            if (success) {

                float orientationMatrix[] = new float[3];

                // Returns the device's orientation given
                // the rotationMatrix

                SensorManager.getOrientation(rotationMatrix, orientationMatrix);

                // Get the rotation, measured in radians, around the Z-axis
                // Note: This assumes the device is held flat and parallel
                // to the ground

                float rotationInRadians = orientationMatrix[0];

                // Convert from radians to degrees
                mRotationInDegress = Math.toDegrees(rotationInRadians);

                // Request redraw
                mCompassArrow.invalidate();

                // Reset sensor event data arrays
                mGravity = mGeomagnetic = null;

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // N/A
    }

    public class CompassArrowView extends View {

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.arrow);
        int mBitmapWidth = mBitmap.getWidth();

        // Height and Width of Main View
        int mParentWidth;
        int mParentHeight;

        // Center of Main View
        int mParentCenterX;
        int mParentCenterY;

        // Top left position of this View
        int mViewTopX;
        int mViewLeftY;

        public CompassArrowView(Context context) {
            super(context);
        };

        // Compute location of compass arrow
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mParentWidth = mFrame.getWidth();
            mParentHeight = mFrame.getHeight();

            mParentCenterX = mParentWidth / 2;
            mParentCenterY = mParentHeight / 2;

            mViewLeftY = mParentCenterX - mBitmapWidth / 2;
            mViewTopX = mParentCenterY - mBitmapWidth / 2;
        }

        // Redraw the compass arrow
        @Override
        protected void onDraw(Canvas canvas) {

            // Save the canvas
            canvas.save();

            // Rotate this View
            canvas.rotate((float) -mRotationInDegress, mParentCenterX,
                    mParentCenterY);

            // Redraw this View
            canvas.drawBitmap(mBitmap, mViewLeftY, mViewTopX, null);

            // Restore the canvas
            canvas.restore();

        }
    }
}