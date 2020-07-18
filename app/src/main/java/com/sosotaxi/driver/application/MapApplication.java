package com.sosotaxi.driver.application;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.TTSUtility;

/**
 * 用于百度地图SDK初始化
 */
public class MapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);

        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SpeechConstant.APPID +"="+getString(R.string.app_id));
        // stringBuffer.append(",");
        //stringBuffer.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(MapApplication.this, stringBuffer.toString());

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
