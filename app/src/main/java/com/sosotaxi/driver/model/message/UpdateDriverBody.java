/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
package com.sosotaxi.driver.model.message;

import com.google.gson.annotations.SerializedName;
import com.sosotaxi.driver.model.message.BaseBody;


public class UpdateDriverBody extends BaseBody {
    private long messageId;
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;
    private boolean isDispatched;
    private boolean startListening;
    private int serverType;

    public void setServerType(int serverType) {
        this.serverType = serverType;
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
