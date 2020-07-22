package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.LocationPoint;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class CheckBondedDriverGeoBody extends BaseBody{
    private String userToken;
    private LocationPoint geoPoint;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public LocationPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(LocationPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
