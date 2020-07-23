package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Order;

public class ArriveDestPointMessageResponseBody extends BaseBody {

    private Order order;

    private Double basicCost;

    private Double freewayCost;

    private Double parkingCost;
}
