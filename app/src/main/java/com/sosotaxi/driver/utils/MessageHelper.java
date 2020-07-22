package com.sosotaxi.driver.utils;

import com.google.gson.Gson;
import com.sosotaxi.driver.model.message.BaseBody;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.service.net.DriverOrderClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class MessageHelper {
    private static MessageHelper sInstance;
    private ConcurrentLinkedQueue<BaseMessage> mSendMessageQueue;
    private ConcurrentHashMap<Integer,BaseMessage> mSentMessageMap;
    private AtomicInteger atomicInteger;
    private DriverOrderClient mClient;
    private Gson gson;

    public static MessageHelper getInstance(){
        if(sInstance==null){
            sInstance=new MessageHelper();
        }
        return  sInstance;
    }

    public MessageHelper(){
        mSendMessageQueue=new ConcurrentLinkedQueue<>();
        mSentMessageMap=new ConcurrentHashMap<>();
        atomicInteger=new AtomicInteger();
        gson=new Gson();
    }

    public BaseMessage build(MessageType type, BaseBody body){
        BaseMessage message=new BaseMessage(type, body);
        return message;
    }

    public int getMessageId(){
        return atomicInteger.getAndIncrement();
    }

    public void send(MessageType type, BaseBody body){
        BaseMessage message=new BaseMessage(type, body);
        send(message);
    }

    public void send(BaseMessage message){
        mSendMessageQueue.add(message);
        String json=gson.toJson(message);
        if(mClient!=null){
            mClient.send(json);
        }
    }

    public void setClient(DriverOrderClient client){
        mClient=client;
    }
}
