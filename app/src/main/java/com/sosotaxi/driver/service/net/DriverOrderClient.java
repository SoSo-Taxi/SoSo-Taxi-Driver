/**
 * @Author 范承祥
 * @CreateTime 2020/7/19
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.service.net;

import android.content.Intent;
import android.util.Log;

import com.sosotaxi.driver.common.Constant;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * 司机订单连接器
 */
public class DriverOrderClient extends WebSocketClient {
    public DriverOrderClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // 连接建立
        Log.d("MESSAGE",handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        // 收到信息
        Log.d("MESSAGE",message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // 连接关闭
        Log.d("MESSAGE",code+", "+reason+", "+remote);
    }

    @Override
    public void onError(Exception ex) {
        // 发生错误
        Log.d("MESSAGE",ex.getMessage());
    }
}
