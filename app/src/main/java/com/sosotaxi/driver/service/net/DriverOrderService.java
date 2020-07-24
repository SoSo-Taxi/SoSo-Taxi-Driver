package com.sosotaxi.driver.service.net;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sosotaxi.driver.common.Constant;

import java.net.URI;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/19
 * @UpdateTime 2020/7/19
 */
public class DriverOrderService extends Service {

    private URI mUri;
    private DriverOrderClient mDriverOrderClient;
    private DriverOrderBinder mDriverOrderBinder;

    public DriverOrderService(){
        mDriverOrderBinder=new DriverOrderBinder();
    }

    public DriverOrderClient getClient(){
        return mDriverOrderClient;
    }

    private void connect(){
        new Thread(){
            @Override
            public void run() {
                try{
                    mDriverOrderClient.connectBlocking();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mDriverOrderBinder;
    }

    public class DriverOrderBinder extends Binder {
        public DriverOrderService getService(String token){

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

            connect();
            return DriverOrderService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }
}
