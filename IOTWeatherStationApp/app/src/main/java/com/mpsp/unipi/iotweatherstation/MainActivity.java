package com.mpsp.unipi.iotweatherstation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    Handler h = new Handler();
    int delay = 5000; //15 seconds
    Runnable runnable;
    int counter = 0;
    //private DBManager dbManager;
    FirebaseDatabase database;
    DatabaseReference myRef;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //dbManager = new DBManager(this);
        //dbManager.open();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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
                bringData();
            }
        });
    }

    private void bringData() {
        final Context context = getApplicationContext();
        final int duration = Toast.LENGTH_LONG;
        boolean wifiOn = checkWifiOn();
        boolean connectedToSSID = checkConnectedToWeatherStation();

        if (wifiOn && connectedToSSID) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Syncing...");
            progressDialog.show();

            final TextView mTextView = (TextView) findViewById(R.id.textView);
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

                                tempTextView.setText(tempe);
                                humTextView.setText(humi);
                                lumTextView.setText(lumi);


                                DatabaseReference childRef = myRef.push();
                                IotData iotdata = new IotData(tempe, humi, lumi);
                                childRef.setValue(iotdata);

                                //dbManager.insert(Float.parseFloat(tempe), Float.parseFloat(humi), Float.parseFloat(lumi));

                                CharSequence text = "Done!";
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();

                                progressDialog.dismiss();
                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mTextView.setText("That didn't work!");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            if (!wifiOn) {
                CharSequence text = "Please check wifi connectivity";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            if (!connectedToSSID && wifiOn) {
                CharSequence text = "Please connect to IOT Weather Station";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
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

        h.postDelayed(new Runnable() {
            public void run() {
                //do something

                Context context = getApplicationContext();
                CharSequence text = "Hello #" + counter++;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onStart();
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
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

                return true;
            case R.id.about:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void about() {
        Intent i = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(i);
    }
}
