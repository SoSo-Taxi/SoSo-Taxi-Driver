package com.sosotaxi.driver.model;

import com.baidu.trace.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/21
 */
public class LocationPoint {
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;

    public LocationPoint(LatLng location){
        this.latitude=location.getLatitude();
        this.longitude=location.getLongitude();
    }

    public LocationPoint(double latitude,double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
