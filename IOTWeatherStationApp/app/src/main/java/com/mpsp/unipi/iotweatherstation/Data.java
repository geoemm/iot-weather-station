package com.mpsp.unipi.iotweatherstation;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 14/11/2016.
 */

public class Data {
    public String tempeture;
    public String humidity;
    public String ambientLight;
    public String userId;

    public Map<String, Boolean> stars = new HashMap<>();

    public Data(){

    }

    public Data(String tempeture, String humidity, String ambientLight,String userId){

        this.userId=userId;
        this.tempeture=tempeture;
        this.humidity=humidity;
        this.ambientLight=ambientLight;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tempeture", tempeture);
        result.put("humidity", humidity);
        result.put("ambientLight", ambientLight);
        result.put("userId", userId);

        return result;
    }
}
