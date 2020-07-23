/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Order;

/**
 * 接到乘客请求主体
 */
public class PickUpPassengerBody extends BaseBody{
    /**
     * 订单
     */
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
