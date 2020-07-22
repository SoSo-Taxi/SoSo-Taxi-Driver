package com.sosotaxi.driver.service.net;

import android.content.Intent;
import android.util.Log;

import com.sosotaxi.driver.common.Constant;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/19
 * @UpdateTime 2020/7/19
 */
public class DriverOrderClient extends WebSocketClient {
    public DriverOrderClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d("WSTEST",handshakedata.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        Log.d("WSTEST",message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("WSTEST",code+", "+reason+", "+remote);
    }

    @Override
    public void onError(Exception ex) {
        Log.d("WSTEST",ex.getMessage());
    }
}
