/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.ui.driverOrder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.OnToolbarListener;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.ServiceType;
import com.sosotaxi.driver.model.message.UpdateDriverBody;
import com.sosotaxi.driver.service.net.DriverOrderClient;
import com.sosotaxi.driver.service.net.DriverOrderService;
import com.sosotaxi.driver.service.net.OrderMessageReceiver;
import com.sosotaxi.driver.service.net.QueryLatestPointTask;
import com.sosotaxi.driver.ui.overlay.TrackOverlay;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.utils.TraceHelper;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;
import com.sosotaxi.driver.viewModel.UserViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverOrderActivity extends AppCompatActivity implements OnToolbarListener {

    private OrderViewModel mOrderViewModel;

    private UserViewModel mUserViewModel;

    private DriverViewModel mDriverViewModel;

    private Toolbar mToolbar;
    private TextView mTextViewTitle;

    private DriverOrderClient mDriverOrderClient;
    private DriverOrderService mDriverOrderService;
    private DriverOrderService.DriverOrderBinder mBinder;
    private OrderMessageReceiver mOrderMessageReceiver;
    private MessageHelper mMessageHelper;

    private Thread mQueryThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);

        mOrderViewModel=new ViewModelProvider(this).get(OrderViewModel.class);
        mUserViewModel=new ViewModelProvider(this).get(UserViewModel.class);
        mDriverViewModel=new ViewModelProvider(this).get(DriverViewModel.class);

        Driver driver=mDriverViewModel.getDriver().getValue();
        Order order=mOrderViewModel.getOrder().getValue();

        mMessageHelper=MessageHelper.getInstance();

        // 绑定服务
        startService();
        bindService();
        registerReceiver();

        mToolbar = findViewById(R.id.toolbarDriverOrder);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTextViewTitle=findViewById(R.id.textViewDriverOrderToolbarTitle);

        // 初始化轨迹记录
        TraceHelper.initTrace(mUserViewModel.getUser().getValue().getUserName(),Constant.GATHER_INTERVAL,Constant.PACK_INTERVAL,onTraceListener);
        // 开始记录轨迹
        TraceHelper.startTrace();

        // 跳转到达上车地点界面
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutDriverOrder,new ReceiveOrderFragment(),null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // 返回上一级页面
                FragmentManager fragmentManager=getSupportFragmentManager();
                getSupportFragmentManager().popBackStack();
                if(fragmentManager.getBackStackEntryCount()==1){
                    showBackButton(false);
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 断开连接
        unbindService(serviceConnection);
        if(mOrderMessageReceiver!=null){
            unregisterReceiver(mOrderMessageReceiver);
        }
        TraceHelper.stopGather();
        TraceHelper.stopTrace();
        if(mQueryThread.isInterrupted()==false){
            mQueryThread.interrupt();
        }
    }

    /**
     * 设置工具栏标题
     * @param title 标题
     */
    @Override
    public void setTitle(String title) {
        mTextViewTitle.setText(title);
    }

    /**
     * 设置工具栏是否展示
     * @param isShown 是否展示
     */
    @Override
    public void showToolbar(boolean isShown) {
        if(isShown==false){
            getSupportActionBar().hide();
        }else{
            getSupportActionBar().show();
        }
    }

    /**
     * 设置工具栏返回按钮是否显示
     * @param isShown 是否展示
     */
    @Override
    public void showBackButton(boolean isShown) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isShown);
    }

    /**
     * 开启WebSocket服务
     */
    private void startService(){
        Intent intent=new Intent(getApplicationContext(),DriverOrderService.class);
        startService(intent);
    }

    /**
     * 绑定服务
     */
    private void bindService(){
        Intent intent = new Intent(getApplicationContext(), DriverOrderService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 注册广播接收器
     */
    private void registerReceiver(){
        mOrderMessageReceiver=new OrderMessageReceiver();
        IntentFilter intentFilter=new IntentFilter(Constant.FILTER_CONTENT);
        registerReceiver(mOrderMessageReceiver,intentFilter);
    }

    // 服务连接监听器
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder=(DriverOrderService.DriverOrderBinder)service;
            mDriverOrderService=mBinder.getService(mUserViewModel.getUser().getValue().getToken());
            mDriverOrderClient=mDriverOrderService.getClient();
            mMessageHelper.setClient(mDriverOrderClient);
            Toast.makeText(getApplicationContext(),"Service已连接",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(),"Service已断开",Toast.LENGTH_SHORT).show();
        }
    };

    // 初始化轨迹服务监听器
    OnTraceListener onTraceListener = new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {

        }

        // 开启服务回调
        @Override
        public void onStartTraceCallback(int status, String message) {
            if(status==0){
                Toast.makeText(getApplicationContext(), "开启鹰眼服务成功", Toast.LENGTH_SHORT).show();
                TraceHelper.startGather();

            }else{
                Toast.makeText(getApplicationContext(), "开启鹰眼服务失败", Toast.LENGTH_SHORT).show();
            }

        }
        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {
            //Toast.makeText(getContext(), "停止鹰眼服务", Toast.LENGTH_SHORT).show();
        }
        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            Toast.makeText(getApplicationContext(), "开始收集", Toast.LENGTH_SHORT).show();
            // 开始上传定位
            mQueryThread=new Thread(new QueryLatestPointTask(Constant.GATHER_INTERVAL*1000,mUserViewModel.getUser().getValue().getUserName(),onTrackListener));
            mQueryThread.start();
        }
        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
            //Toast.makeText(getContext(), "停止收集", Toast.LENGTH_SHORT).show();
        }

        // 推送回调
        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {

        }

        @Override
        public void onInitBOSCallback(int i, String s) {

        }
    };

    // 轨迹查询结果监听器
    OnTrackListener onTrackListener=new OnTrackListener() {
        @Override
        public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
        }

        @Override
        public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
            if(latestPointResponse.status!=StatusCodes.SUCCESS){
                return;
            }

            // 封装消息
            com.baidu.trace.model.LatLng location=latestPointResponse.getLatestPoint().getLocation();
            Driver driver=mDriverViewModel.getDriver().getValue();
            driver.setCurrentPoint(new LocationPoint(location));
            UpdateDriverBody body=new UpdateDriverBody();
            body.setMessageId(mMessageHelper.getMessageId());
            body.setDispatched(true);
            body.setStartListening(true);
            body.setServerType(driver.getServiceType());
            body.setLatitude(location.getLatitude());
            body.setLongitude(location.getLongitude());
            BaseMessage message=mMessageHelper.build(MessageType.UPDATE_REQUEST,body);

            // 发送消息
            mMessageHelper.send(message);
        }
    };

    // 自定义属性监听器
    OnCustomAttributeListener mCustomAttributeListener = new OnCustomAttributeListener() {
        @Override
        public Map<String, String> onTrackAttributeCallback() {
            Map<String, String> trackAttrs = new HashMap<String, String>();

            //获取司机是否接单
            String isAvailable = String.valueOf(mDriverViewModel.getDriver().getValue().getAvailable());
            //随轨迹上传接单信息
            trackAttrs.put("is_available", isAvailable);

            //获取司机是否接单
            String serviceType = String.valueOf(mDriverViewModel.getDriver().getValue().getServiceType());
            trackAttrs.put("service_type", serviceType);

            return trackAttrs;
        }

        @Override
        public Map<String, String> onTrackAttributeCallback(long l) {
            return null;
        }
    };

    public DriverOrderClient getClient(){
        return mDriverOrderClient;
    }
}