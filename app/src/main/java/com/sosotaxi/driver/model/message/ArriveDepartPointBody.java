/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Order;

/**
 * 到达上车点请求主体
 */
public class ArriveDepartPointBody extends BaseBody{
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
