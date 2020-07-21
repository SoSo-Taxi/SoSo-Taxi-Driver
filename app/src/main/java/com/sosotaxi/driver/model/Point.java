package com.sosotaxi.driver.model;

import com.google.gson.annotations.SerializedName;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/21
 */
public class Point {
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;
}
