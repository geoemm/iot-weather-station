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
import android.widget.EditText;
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

    private TextView userIdText;
    private TextView nameText;
    private TextView emailText;
    private EditText tempeture;
    private EditText humidity;
    private EditText ambient;
    private Button sendData;
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

        userIdText = (TextView)findViewById(R.id.userId);
        nameText = (TextView)findViewById(R.id.name);
        emailText = (TextView)findViewById(R.id.email);

        sendData = (Button) findViewById(R.id.sendData);
        tempeture = (EditText) findViewById(R.id.tempeture);
        humidity = (EditText) findViewById(R.id.humidity);
        ambient = (EditText) findViewById(R.id.ambient);


        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReferenceFromUrl("https://iot-weather-station-app.firebaseio.com");
        mDatabaseData=database.getReferenceFromUrl("https://iot-weather-station-app.firebaseio.com/data/");

        writeNewUser(userId,name,email);



        userIdText.setText(userId);
        nameText.setText(name);
        emailText.setText(email);


        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeData(userId,tempeture.getText().toString(), humidity.getText().toString(), ambient.getText().toString());
            }
        });

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

    private void writeData(String userId, String tempeture, String humidity, String ambientLight){
        Data data = new Data(tempeture,humidity,ambientLight,userId);

        mDatabaseData.child(mDatabase.push().getKey()).setValue(data);
    }


}
