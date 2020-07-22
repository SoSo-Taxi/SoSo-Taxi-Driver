package com.sosotaxi.driver.model.message;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class BaseMessage {
    private MessageType type;
    private BaseBody body;

    public BaseMessage(MessageType type,BaseBody body){
        this.type=type;
        this.body=body;
    }
}
