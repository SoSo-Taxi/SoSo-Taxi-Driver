package com.sosotaxi.driver.model.message;

import com.sosotaxi.driver.model.Order;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class AskForDriverBody extends BaseBody {
    private String city;
    private Order order;
}
