package com.sosotaxi.driver.model.message;

import com.google.gson.annotations.SerializedName;
import com.sosotaxi.driver.model.Driver;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class OrderResultBody extends BaseBody{
    private Driver driver;
    private double estimateMoney;
    private long estimateTime;
    @SerializedName("msg")
    private String message;
    private int statusCode;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public double getEstimateMoney() {
        return estimateMoney;
    }

    public void setEstimateMoney(double estimateMoney) {
        this.estimateMoney = estimateMoney;
    }

    public long getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(long estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
