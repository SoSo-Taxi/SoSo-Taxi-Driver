/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.sosotaxi.driver.model.message.BaseBody;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.service.net.DriverOrderClient;

import org.java_websocket.enums.ReadyState;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消息帮手类
 */
public class MessageHelper {
    /** 消息帮手实例 */
    private static MessageHelper sInstance;
    /** 序列生成器 */
    private AtomicInteger atomicInteger;
    /** 连接器 */
    private DriverOrderClient mClient;
    /** Gson对象 */
    private Gson mGson;

    /**
     * 获取实例
     * @return 消息帮手实例
     */
    public static MessageHelper getInstance(){
        if(sInstance==null){
            sInstance=new MessageHelper();
        }
        return  sInstance;
    }

    public MessageHelper(){
        atomicInteger=new AtomicInteger();
        mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").create();
    }

    /**
     * 构建消息
     * @param type 消息类型
     * @param body 消息体
     * @return 消息
     */
    public BaseMessage build(MessageType type, BaseBody body){
        BaseMessage message=new BaseMessage(type, body);
        return message;
    }

    /**
     * 解析消息
     * @param json JSON
     * @return 消息
     */
    public BaseMessage parse(String json){
        BaseMessage message= mGson.fromJson(json,BaseMessage.class);
        return message;
    }

    /**
     * 发送消息
     * @param type 消息类型
     * @param body 消息体
     */
    public void send(MessageType type, BaseBody body){
        BaseMessage message=new BaseMessage(type, body);
        send(message);
    }

    /**
     * 发送消息
     * @param message 消息
     */
    public void send(BaseMessage message){
        String json= mGson.toJson(message);
        Log.d("MESSAGE",json);
        if(mClient!=null){
            try {
                // 发送消息
                mClient.send(json);
            }catch (WebsocketNotConnectedException e){
                // 断开连接则重新连接
                connect();
            }

        }
    }

    /**
     * 设置连接器
     * @param client 连接器
     */
    public void setClient(DriverOrderClient client){
        mClient=client;
    }

    /**
     * 获取消息序号
     * @return 序号
     */
    public int getMessageId(){
        return atomicInteger.getAndIncrement();
    }

    /**
     * 连接
     */
    private void connect() {
        if (mClient == null) {
            return;
        }
        if (!mClient.isOpen()) {
            if (mClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                try {
                    // 首次连接
                    mClient.connect();
                } catch (IllegalStateException e) {
                }
            } else if (mClient.getReadyState().equals(ReadyState.CLOSING) || mClient.getReadyState().equals(ReadyState.CLOSED)) {
                // 断开重连
                mClient.reconnect();
            }
        }
    }
}
