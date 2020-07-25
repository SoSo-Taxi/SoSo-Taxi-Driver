/**
 * @Author 范承祥
 * @CreateTime 2020/7/19
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.service.net;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sosotaxi.driver.common.Constant;

import org.java_websocket.WebSocket;
import org.java_websocket.enums.ReadyState;

import java.net.URI;

/**
 * 司机订单服务
 */
public class DriverOrderService extends Service {

    /**
     * URI
     */
    private URI mUri;

    /**
     * 司机订单连接器
     */
    private DriverOrderClient mDriverOrderClient;

    /**
     * 服务绑定
     */
    private DriverOrderBinder mDriverOrderBinder;

    public DriverOrderService(){
        mDriverOrderBinder=new DriverOrderBinder();
    }

    public DriverOrderClient getClient(){
        return mDriverOrderClient;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDriverOrderBinder;
    }

    public class DriverOrderBinder extends Binder {
        public DriverOrderService getService(String token){
            // 初始化连接器
            mUri=URI.create(Constant.WEB_SOCKET_URI+token);
            mDriverOrderClient = new DriverOrderClient(mUri){
                @Override
                public void onMessage(String message) {
                    Intent intent=new Intent();
                    intent.setAction(Constant.FILTER_CONTENT);
                    intent.putExtra(Constant.EXTRA_RESPONSE_MESSAGE,message);

                    sendBroadcast(intent);
                }
            };

            // 连接
            connect();
            return DriverOrderService.this;
        }
    }

    /**
     * 连接
     */
    private void connect(){
        if (mDriverOrderClient == null) {
            return;
        }
        if (!mDriverOrderClient.isOpen()) {
            if (mDriverOrderClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                try {
                    // 首次连接
                    mDriverOrderClient.connect();
                } catch (IllegalStateException e) {
                }
            } else if (mDriverOrderClient.getReadyState().equals(ReadyState.CLOSING) || mDriverOrderClient.getReadyState().equals(ReadyState.CLOSED)) {
                // 断开重连
                mDriverOrderClient.reconnect();
            }
        }
    }
}
