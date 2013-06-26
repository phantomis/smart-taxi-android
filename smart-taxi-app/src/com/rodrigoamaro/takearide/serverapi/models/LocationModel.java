package com.rodrigoamaro.takearide.serverapi.models;

import java.util.Date;

public class LocationModel {

    public double latitude;
    public double longitude;
    public float speed;
    public long timestamp;
    
    public LocationModel() {}
    
    public LocationModel(double latitude, double longitude, float speed){
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = new Date().getTime();
    }
}
