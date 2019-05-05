package com.example.finalproject489;

import java.io.Serializable;

public class EventNow implements Serializable{

    String description;
    double locationx;
    double locationy;

    public EventNow() {
    }
    public EventNow(String Description, double LocationX, double LocationY) {
        description = Description;
        locationx = LocationX;
        locationy = LocationY;
    }
    public String getdescription() {
        return description;
    }
    public double getlocationx() {
        return locationx;
    }
    public double getlocationy() {
        return locationy;
    }
}
