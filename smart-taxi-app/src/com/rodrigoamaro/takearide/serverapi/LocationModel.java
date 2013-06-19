package com.rodrigoamaro.takearide.serverapi;

import java.util.Date;

public class LocationModel {

    double latitude;
    double longitude;
    float speed;
    long timestamp;
    
    public LocationModel() {}
    
    public LocationModel(double latitude, double longitude, float speed){
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = new Date().getTime();
    }
}
