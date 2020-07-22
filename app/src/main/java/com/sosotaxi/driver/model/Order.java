package com.sosotaxi.driver.model;

import com.google.gson.annotations.SerializedName;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/21
 */
public class Order {
    private long orderId;
    private long createTime;
    private OrderStatus status;
    private String city;
    @SerializedName("passengerNum")
    private int passengerNumber;
    @SerializedName("departPoint")
    private LocationPoint departPoint;
    @SerializedName("destPoint")
    private LocationPoint destinationPoint;
    private long departTime;
    private String departName;
    private String destinationName;
    private int serviceType;
    private long passengerId;
    private String passengerName;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPassengerNumber() {
        return passengerNumber;
    }

    public void setPassengerNumber(int passengerNumber) {
        this.passengerNumber = passengerNumber;
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

    public long getDepartTime() {
        return departTime;
    }

    public void setDepartTime(long departTime) {
        this.departTime = departTime;
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

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(long passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }
}
