/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/23
 */
package com.sosotaxi.driver.model.message;

import com.google.gson.annotations.SerializedName;
import com.sosotaxi.driver.model.message.BaseBody;

/**
 * 更新司机信息请求主体
 */
public class UpdateDriverBody extends BaseBody {
    /**
     * 消息ID
     */
    private long messageId;

    /**
     * 纬度
     */
    @SerializedName("lat")
    private double latitude;

    /**
     * 经度
     */
    @SerializedName("lng")
    private double longitude;

    /**
     * 是否接单
     */
    private boolean isDispatched;

    /**
     * 是否听单
     */
    private boolean startListening;

    /**
     * 服务类型
     */
    private Short serviceType;

    public Short getServiceType() {
        return serviceType;
    }

    public void setServiceType(Short serviceType) {
        this.serviceType = serviceType;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
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

    public boolean isDispatched() {
        return isDispatched;
    }

    public void setDispatched(boolean dispatched) {
        isDispatched = dispatched;
    }

    public boolean isStartListening() {
        return startListening;
    }

    public void setStartListening(boolean startListening) {
        this.startListening = startListening;
    }

}
