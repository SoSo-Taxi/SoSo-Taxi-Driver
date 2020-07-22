package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.Order;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class DriverAnswerOrderBody extends BaseBody{
    private boolean takeOrder;
    private Order order;
    private Driver driver;

    public boolean isTakeOrder() {
        return takeOrder;
    }

    public void setTakeOrder(boolean takeOrder) {
        this.takeOrder = takeOrder;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
