package com.sosotaxi.driver.model.message;

import com.google.gson.annotations.SerializedName;
import com.sosotaxi.driver.model.message.BaseBody;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class UpdateDriverResponseBody extends BaseBody {
    private long messageId;
    @SerializedName("msg")
    private String message;
    private int statusCode;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
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
