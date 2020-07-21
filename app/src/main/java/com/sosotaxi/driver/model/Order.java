package com.sosotaxi.driver.model;

import com.baidu.mapapi.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/21
 */
public class Order {
    private long orderId;
    private String city;
    @SerializedName("passengerNum")
    private short passengerNumber;
    private Point departPoint;
    private Point destPoint;
    private Date departTime;
    private String departName;
    private String destName;
    private short serviceType;
    private long passengerId;

    public long getOrderId() {
        return orderId;
    }

    public String getCity() {
        return city;
    }

    public short getPassengerNumber() {
        return passengerNumber;
    }

    public Point getDepartPoint() {
        return departPoint;
    }

    public Point getDestPoint() {
        return destPoint;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public String getDepartName() {
        return departName;
    }

    public String getDestName() {
        return destName;
    }

    public short getServiceType() {
        return serviceType;
    }

    public long getPassengerId() {
        return passengerId;
    }
}
