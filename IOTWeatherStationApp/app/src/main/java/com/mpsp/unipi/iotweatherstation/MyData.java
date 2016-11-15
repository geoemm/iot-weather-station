package com.mpsp.unipi.iotweatherstation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyData extends AppCompatActivity {
    private String userId;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_data);

        userId = getIntent().getExtras().getString("userId","null");

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReferenceFromUrl("https://iot-weather-station-app.firebaseio.com/users/");
    }
}
