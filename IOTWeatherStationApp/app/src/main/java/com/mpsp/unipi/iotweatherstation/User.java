package com.mpsp.unipi.iotweatherstation;

/**
 * Created by User on 14/11/2016.
 */

public class User {

    public String name;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

}