/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Order;

/**
 * 打车请求主体
 */
public class AskForDriverBody extends BaseBody {
    /**
     * 城市
     */
    private String city;

    /**
     * 乘客手机号
     */
    private String passengerPhoneNumber;

    /**
     * 订单
     */
    private Order order;

    public String getPassengerPhoneNumber() {
        return passengerPhoneNumber;
    }

    public void setPassengerPhoneNumber(String passengerPhoneNumber) {
        this.passengerPhoneNumber = passengerPhoneNumber;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
