package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Order;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class PickUpPassengerBody extends BaseBody{
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
