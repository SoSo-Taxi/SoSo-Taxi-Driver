package com.sosotaxi.driver.model.message;

import android.graphics.Point;

import com.google.gson.annotations.SerializedName;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
public class CheckBondedDriverGeoToBody {
    private int statusCode;

    @SerializedName("msg")
    private String message;

    private Point point;

    private double distance;


}
