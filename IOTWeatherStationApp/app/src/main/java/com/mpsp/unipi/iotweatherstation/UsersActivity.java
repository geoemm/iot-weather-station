package com.mpsp.unipi.iotweatherstation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReferenceFromUrl("https://iot-weather-station-app.firebaseio.com/data/");
    }
}
