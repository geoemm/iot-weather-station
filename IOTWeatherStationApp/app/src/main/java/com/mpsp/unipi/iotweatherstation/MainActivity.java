package com.mpsp.unipi.iotweatherstation;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    int unit;
    private String TAG = MainActivity.class.getSimpleName();
    Handler h = new Handler();
    int delay = 15000; //15 seconds
    Runnable runnable;
    private DBManager dbManager;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private LineChart mChart;
    boolean enableSync;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSettings();
        showMsg();

        if (unit == 0) {
            TextView tv1 = (TextView)findViewById(R.id.tempUnitValue);
            tv1.setText("C");
        } else {
            TextView tv1 = (TextView)findViewById(R.id.tempUnitValue);
            tv1.setText("F");
        }

        mChart = (LineChart) findViewById(R.id.chart);
        Typeface mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        mChart.setOnChartValueSelectedListener(this);

        // enable description text
        //mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

//        // get the legend (only possible after setting data)
//        Legend l = mChart.getLegend();
//
//        // modify the legend ...
//        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(mTfLight);
//        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("iot_weather_data");

        // Assign a listener to detect changes to the child items
        // of the database reference.
        myRef.addChildEventListener(new ChildEventListener(){

            // This function is called once for each child that exists
            // when the listener is added. Then it is called
            // each time a new child is added.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                //String value = dataSnapshot.getValue(String.class);
                //adapter.add(value);
            }

            // This function is called each time a child item is removed.
            public void onChildRemoved(DataSnapshot dataSnapshot){
                String value = dataSnapshot.getValue(String.class);
                //adapter.remove(value);
            }

            // The following functions are also required in ChildEventListener implementations.
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName){}
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName){}

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG:", "Failed to read value.", error.toException());
            }
        });

        final Button button = (Button) findViewById(R.id.syncBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (enableSync) {
                    bringData(true);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please connect to IOT Weather Station", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void startSYNC() {
        h.postDelayed(new Runnable() {
            public void run() {

                bringData(false);
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);
    }

    private void showMsg() {
        if (checkWifiOn() && checkConnectedToWeatherStation()) {
            enableSync = true;
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), "Connected to IOT Weather Station", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SYNC", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Start SYNC
                            startSYNC();
                        }
                    });
            snackbar.show();
        } else {
            if (!checkWifiOn()) {
                enableSync = false;
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "No wifi connection!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("SETTINGS", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                            }
                        });
                snackbar.show();
            }
            if (!checkConnectedToWeatherStation() && checkWifiOn()) {
                enableSync = false;
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "Connect to IOT Weather Station", Snackbar.LENGTH_INDEFINITE)
                        .setAction("CONNECT", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                            }
                        });
                snackbar.show();
            }
        }
    }

    private void addEntry(String temperature, String humidity, String luminosity) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set1 = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);
            // set.addEntry(...); // can be called as well

            if (set1 == null) {
                set1 = createSet();
                data.addDataSet(set1);
            }

            if (set2 == null) {
                set2 = createSet2();
                data.addDataSet(set2);
            }

            if (set3 == null) {
                set3 = createSet3();
                data.addDataSet(set3);
            }

            data.addEntry(new Entry(set1.getEntryCount(), Float.parseFloat(temperature)), 0);
            data.addEntry(new Entry(set2.getEntryCount(), Float.parseFloat(humidity)), 1);
            data.addEntry(new Entry(set3.getEntryCount(), Float.parseFloat(luminosity)), 2);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(120);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set1 = new LineDataSet(null, "Temperature");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.RED);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        //set1.setFillFormatter(new MyFillFormatter(0f));
        //set1.setDrawHorizontalHighlightIndicator(false);
        //set1.setVisible(false);
        //set1.setCircleHoleColor(Color.WHITE);
        return set1;
    }

    private LineDataSet createSet2() {

        LineDataSet set2 = new LineDataSet(null, "Humidity");
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(Color.BLUE);
        set2.setCircleColor(Color.BLUE);
        set2.setLineWidth(2f);
        set2.setCircleRadius(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        //set2.setFillFormatter(new MyFillFormatter(900f));
        return set2;
    }

    private LineDataSet createSet3() {

        LineDataSet set3 = new LineDataSet(null, "Luminosity");

        set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set3.setColor(Color.GREEN);
        set3.setCircleColor(Color.GREEN);
        set3.setLineWidth(2f);
        set3.setCircleRadius(3f);
        set3.setFillAlpha(65);
        set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
        set3.setDrawCircleHole(false);
        set3.setHighLightColor(Color.rgb(244, 117, 117));
        return set3;
    }

    private void bringData(final boolean showmsg) {
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_LONG;

        if (showmsg) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Syncing...");
            progressDialog.show();
        }


        //final TextView mTextView = (TextView) findViewById(R.id.textView);
        final TextView tempTextView = (TextView) findViewById(R.id.tempValue);
        final TextView humTextView = (TextView) findViewById(R.id.humValue);
        final TextView lumTextView = (TextView) findViewById(R.id.lightValue);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.4.1:8080";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,10));
                        try {
                            JSONObject jObject = new JSONObject(response);
                            JSONObject iotDataObj = jObject.getJSONObject("iot_data");
                            String tempe = iotDataObj.getString("temperature");
                            String humi = iotDataObj.getString("humidity");
                            String lumi = iotDataObj.getString("luminosity");

                            if (unit == 0) {
                                tempTextView.setText(tempe);
                            } else {
                                tempTextView.setText(String.valueOf(Float.parseFloat(tempe) * 1.8 + 32));
                            }

                            humTextView.setText(humi);
                            lumTextView.setText(lumi);


                            DatabaseReference childRef = myRef.push();
                            IotData iotdata = new IotData(tempe, humi, lumi);
                            childRef.setValue(iotdata);
                            addEntry(tempe, humi, lumi);
                            //dbManager.insert(Float.parseFloat(tempe), Float.parseFloat(humi), Float.parseFloat(lumi));

                            CharSequence text = "Done!";
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            if (showmsg) { progressDialog.dismiss(); }
                        } catch (JSONException e) {
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private boolean checkConnectedToWeatherStation() {
        String IOTNetwork = "IOT Weather Station";
        WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid  = info.getSSID();
        String mod_ssid = ssid.substring(1, ssid.length()-1);
        if (mod_ssid.equals(IOTNetwork)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWifiOn() {
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    }

    @Override
    protected void onStart() {
        //start handler as activity become visible
        super.onStart();
        showMsg();
    }

    @Override
    protected void onPause() {
        super.onPause();
        h.removeCallbacks(runnable);
        dbManager.close();
    }

    void getSettings() {
        dbManager = new DBManager(this);
        dbManager.open();
        //dbManager.insert(0,5000);
        try {
            Cursor cursor = dbManager.fetch();
            cursor.moveToFirst();
            unit = cursor.getInt(cursor.getColumnIndex("temperature_unit"));
            delay = cursor.getInt(cursor.getColumnIndex("sync_period"));
            dbManager.close();
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            dbManager.insert(0,5000);
            Cursor cursor = dbManager.fetch();
            cursor.moveToFirst();
            unit = cursor.getInt(cursor.getColumnIndex("temperature_unit"));
            delay = cursor.getInt(cursor.getColumnIndex("sync_period"));
            dbManager.close();
        }
        Toast.makeText(getApplicationContext(), unit + " " + delay, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSettings();
        if (unit == 0) {
            TextView tv1 = (TextView)findViewById(R.id.tempUnitValue);
            tv1.setText("C");
        } else {
            TextView tv1 = (TextView)findViewById(R.id.tempUnitValue);
            tv1.setText("F");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropdownmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                settings();
                return true;
            case R.id.about:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void settings() {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    public void about() {
        Intent i = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(i);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

}
