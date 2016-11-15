package com.mpsp.unipi.iotweatherstation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    public String userId;
    public String name;
    public String email;

    private ArrayList<String> arrayUsers = new ArrayList<>();
    private TextView userIdText;
    private TextView nameText;
    private TextView emailText, cloudText;
    private ListView listView;
    private Button sendData,users,lastData;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getExtras().getString("userId","null");
        name = getIntent().getExtras().getString("name","null");
        email = getIntent().getExtras().getString("email","null");

         database = FirebaseDatabase.getInstance();
         mDatabase = database.getReferenceFromUrl("https://iot-weather-station-app.firebaseio.com");
         mDatabaseData=database.getReferenceFromUrl("https://iot-weather-station-app.firebaseio.com/data/");
         writeNewUser(userId,name,email);

        writeData(userId,"02","365", "6757", "6587");

/*
        listView = (ListView)findViewById(R.id.listView);
        final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arrayUsers);
        listView.setAdapter(mArrayAdapter);
*/



        userIdText = (TextView)findViewById(R.id.userId);
        nameText = (TextView)findViewById(R.id.name);
        emailText = (TextView)findViewById(R.id.email);

        cloudText = (TextView)findViewById(R.id.cloudTextview);
        users = (Button) findViewById(R.id.users);
        lastData = (Button) findViewById(R.id.lastData);
        sendData = (Button) findViewById(R.id.sendData);

        userIdText.setText(userId);
        nameText.setText(name);
        emailText.setText(email);


        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UsersActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        lastData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyData.class);
                intent.putExtra("userId",userId);
            }
        });


        final Button button = (Button) findViewById(R.id.syncBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                bringData();
            }
        });
    }

    private void bringData() {
        final TextView mTextView = (TextView) findViewById(R.id.textView);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.4.1:8080";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: "+ response.substring(0,10));
                        //mTextView.setText("Response is: " + ((response.length()>499)?response.substring(0,500):response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
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


    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void writeData(String userId, String dataId, String tempeture, String humidity, String ambientLight){
        Data data = new Data(tempeture,humidity,ambientLight,userId);

        mDatabaseData.child(mDatabase.push().getKey()).setValue(data);
    }


}
