/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
package com.sosotaxi.driver.model.message;

import com.google.gson.annotations.SerializedName;

/**
 * 错误响应主体
 */
public class ErrorResponseBody extends BaseBody {
    /**
     * 消息
     */
    @SerializedName("msg")
    private String message;

    /**
     * 状态码
     */
    private int statusCode;

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
