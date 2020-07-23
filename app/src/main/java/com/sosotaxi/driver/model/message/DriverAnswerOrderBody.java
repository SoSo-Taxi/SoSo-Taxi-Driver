/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;
import com.sosotaxi.driver.model.Order;

/**
 * 司机响应订单请求主体
 */
public class DriverAnswerOrderBody extends BaseBody{
    /**
     * 是否接单
     */
    private Boolean takeOrder;

    /**
     * 订单
     */
    private Order order;

    /**
     * 司机
     */
    private DriverVo driver;

    public Boolean isTakeOrder() {
        return takeOrder;
    }

    public void setTakeOrder(Boolean takeOrder) {
        this.takeOrder = takeOrder;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Boolean getTakeOrder() {
        return takeOrder;
    }

    public DriverVo getDriver() {
        return driver;
    }

    public void setDriver(DriverVo driver) {
        this.driver = driver;
    }
}
