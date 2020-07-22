package com.sosotaxi.driver.model.message;

import com.google.gson.annotations.SerializedName;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.message.BaseBody;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class StartOrderBody extends BaseBody {
    private String userToken;
    private String city;
    private long passengerId;
    private long departTime;
    private String userName;
    private LocationPoint departPoint;
    @SerializedName("destPoint")
    private LocationPoint destinationPoint;
    private int serviceType;
    private int passengerNum;
    private String departName;
    @SerializedName("destName")
    private String destinationName;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(long passengerId) {
        this.passengerId = passengerId;
    }

    public long getDepartTime() {
        return departTime;
    }

    public void setDepartTime(long departTime) {
        this.departTime = departTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocationPoint getDepartPoint() {
        return departPoint;
    }

    public void setDepartPoint(LocationPoint departPoint) {
        this.departPoint = departPoint;
    }

    public LocationPoint getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(LocationPoint destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getPassengerNum() {
        return passengerNum;
    }

    public void setPassengerNum(int passengerNum) {
        this.passengerNum = passengerNum;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
