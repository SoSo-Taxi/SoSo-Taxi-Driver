/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
package com.sosotaxi.driver.model.message;

/**
 * 消息类型
 */
public enum MessageType {
    /**
     * 司机更新自身
     */
    UPDATE_REQUEST("UPDATE_REQUEST"),

    /**
     * 司机收到回应
     */
    UPDATE_RESPONSE("UPDATE_RESPONSE"),

    /**
     * 乘客发送打车
     */
    START_ORDER_MESSAGE("START_ORDER_MESSAGE"),

    /**
     * 请求打车
     */
    ASK_FOR_DRIVER_MESSAGE("ASK_FOR_DRIVER_MESSAGE"),

    /**
     * 获取所有司机位置
     */
    DRIVER_ANSWER_ORDER_MESSAGE("DRIVER_ANSWER_ORDER_MESSAGE"),

    /**
     * 乘客收到订单结果
     */
    ORDER_RESULT_MESSAGE("ORDER_RESULT_MESSAGE"),

    /**
     * 获取所有司机位置
     */
    GET_ALL_DRIVER_MESSAGE("GET_ALL_DRIVER_MESSAGE"),

    /**
     * 获取订单司机与自己距离
     */
    CHECK_BONDED_DRIVER_GEO_MESSAGE("CHECK_BONDED_DRIVER_GEO_MESSAGE"),

    /**
     * 司机到达目的地
     */
    ARRIVE_DEPART_POINT_MESSAGE("ARRIVE_DEPART_POINT_MESSAGE"),

    /**
     * 司机已到达上车点
     */
    ARRIVE_DEPART_POINT_TO_MESSAGE("ARRIVE_DEPART_POINT_TO_MESSAGE"),

    /**
     * 司机接到乘客
     */
    PICK_UP_PASSENGER_MESSAGE("PICK_UP_PASSENGER_MESSAGE");

    /**
     * 别名
     */
    private String mName;

    MessageType(String name){
        mName=name;
    }

    @Override
    public String toString(){
        return mName;
    }
}
