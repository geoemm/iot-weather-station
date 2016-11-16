package com.mpsp.unipi.iotweatherstation;

import java.text.DateFormat;
import java.util.Date;

public class IotData {

    public String temperature;
    public String humidity;
    public String luminosity;
    public String currentDateTimeString;

    public IotData() {
        // Default constructor required for calls to DataSnapshot.getValue(IotData.class)
    }

    public IotData(String temperature, String humidity, String luminosity) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminosity = luminosity;
        this.currentDateTimeString =  DateFormat.getDateTimeInstance().format(new Date());
    }

}
