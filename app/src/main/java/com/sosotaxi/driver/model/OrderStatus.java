package com.sosotaxi.driver.model;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public enum OrderStatus {
    NOT_START,
    RECEIVE,
    ARRIVE_STARTING_POINT,
    PICK_UP_PASSENGER,
    ARRIVE_DESTINATION,
    PAID;

    @Override
    public String toString(){
        return String.valueOf(this.ordinal());
    }
}
