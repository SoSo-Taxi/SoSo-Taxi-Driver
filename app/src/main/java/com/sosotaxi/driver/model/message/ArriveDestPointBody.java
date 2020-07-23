/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Order;

/**
 * 到达目的地请求主体
 */
public class ArriveDestPointBody extends BaseBody {

    /**
     * 订单
     */
    private Order order;

    /**
     * 基础费用
     */
    private Double basicCost;

    /**
     * 过路费
     */
    private Double freewayCost;

    /**
     * 停车费
     */
    private Double parkingCost;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Double getBasicCost() {
        return basicCost;
    }

    public void setBasicCost(Double basicCost) {
        this.basicCost = basicCost;
    }

    public Double getFreewayCost() {
        return freewayCost;
    }

    public void setFreewayCost(Double freewayCost) {
        this.freewayCost = freewayCost;
    }

    public Double getParkingCost() {
        return parkingCost;
    }

    public void setParkingCost(Double parkingCost) {
        this.parkingCost = parkingCost;
    }
}
