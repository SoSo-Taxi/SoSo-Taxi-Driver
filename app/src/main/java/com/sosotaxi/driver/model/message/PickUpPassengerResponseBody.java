package com.sosotaxi.driver.model.message;
/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
import com.google.gson.annotations.SerializedName;
import com.sosotaxi.driver.model.Order;

/**
 * 接到乘客响应主体
 */
public class PickUpPassengerResponseBody extends BaseBody{
    /**
     * 订单
     */
    private Order order;

    /**
     * 消息
     */
    @SerializedName("msg")
    private String message;

    /**
     * 状态码
     */
    private int statusCode;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
