package com.sosotaxi.driver.application;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.TTSUtility;
import com.sosotaxi.driver.utils.NavigationHelper;
import com.sosotaxi.driver.utils.TraceHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于百度地图SDK初始化
 */
public class MapApplication extends Application {

    /**
     * 轨迹
     */
    private Trace mTrace;

    /**
     * 轨迹连接器
     */
    private LBSTraceClient mTraceClient;

    /**
     * 上下文
     */
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext=getApplicationContext();

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(mContext);

        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SpeechConstant.APPID +"="+getString(R.string.app_id));
        // stringBuffer.append(",");
        //stringBuffer.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(MapApplication.this, stringBuffer.toString());

        // 初始化鹰眼轨迹
        initTrace();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * 初始化鹰眼轨迹
     */
    private void initTrace(){
        mTrace = new Trace(Constant.SERVICE_ID,"");
        mTraceClient = new LBSTraceClient(mContext);
        TraceHelper.setTrace(mTrace);
        TraceHelper.setTraceClient(mTraceClient);
    }
}
