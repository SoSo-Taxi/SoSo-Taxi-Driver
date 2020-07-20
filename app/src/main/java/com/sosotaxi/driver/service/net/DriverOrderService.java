package com.sosotaxi.driver.service.net;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

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

    public DriverOrderService(String uri){
        if(uri.isEmpty()){
            return;
        }
        mUri=URI.create(uri);
        mDriverOrderClient=new DriverOrderClient(mUri);
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
        public DriverOrderService getService(){
            return DriverOrderService.this;
        }
    }
}
