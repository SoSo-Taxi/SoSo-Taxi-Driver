/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
package com.sosotaxi.driver.model.message;

import com.google.gson.annotations.SerializedName;
import com.sosotaxi.driver.model.LocationPoint;

/**
 * 接单司机请求主体
 */
public class DispatchDriverBody extends BaseBody{
    /**
     * 起点
     */
    private LocationPoint departPoint;

    /**
     * 终点
     */
    @SerializedName("destPoint")
    private LocationPoint destinationPoint;

    /**
     * 用户名
     */
    private String userName;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
