/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.ui.driverOrder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.OnToolbarListener;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.UpdateDriverBody;
import com.sosotaxi.driver.service.net.DriverOrderClient;
import com.sosotaxi.driver.service.net.DriverOrderService;
import com.sosotaxi.driver.service.net.QueryLatestPointTask;
import com.sosotaxi.driver.ui.overlay.TrackOverlay;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.utils.TraceHelper;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;
import com.sosotaxi.driver.viewModel.UserViewModel;

import java.util.HashMap;
import java.util.Map;

public class DriverOrderActivity extends AppCompatActivity implements OnToolbarListener {

    /**
     * 订单ViewModel
     */
    private OrderViewModel mOrderViewModel;

    /**
     * 用户ViewModel
     */
    private UserViewModel mUserViewModel;

    /**
     * 司机ViewModel
     */
    private DriverViewModel mDriverViewModel;

    /**
     * 订单连接器
     */
    private DriverOrderClient mDriverOrderClient;

    /**
     * 订单服务
     */
    private DriverOrderService mDriverOrderService;

    /**
     * 服务绑定
     */
    private DriverOrderService.DriverOrderBinder mBinder;

    /**
     * 广播接收器
     */
    private BroadcastReceiver mBroadcastReceiver;

    /**
     * 消息帮手对象
     */
    private MessageHelper mMessageHelper;

    /**
     * 实时定位查询
     */
    private QueryLatestPointTask mQueryLatestPointTask;

    /**
     * 定位覆盖对象
     */
    private TrackOverlay mTrackOverlay;

    private Toolbar mToolbar;
    private TextView mTextViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);

        // 获取ViewModel
        mOrderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mDriverViewModel = new ViewModelProvider(this).get(DriverViewModel.class);

        // 获取数据
        Bundle bundle = getIntent().getExtras();
        Order order = bundle.getParcelable(Constant.EXTRA_ORDER);
        Driver driver = bundle.getParcelable(Constant.EXTRA_DRIVER);
        DriverVo driverVo = bundle.getParcelable(Constant.EXTRA_DRIVER_VO);

        // 设置初值
        mOrderViewModel.getOrder().setValue(order);
        mDriverViewModel.getDriver().setValue(driver);
        mDriverViewModel.getDriverVo().setValue(driverVo);

        // 获取消息帮手
        mMessageHelper = MessageHelper.getInstance();

        // 绑定服务
        startService();
        bindService();
        registerReceiver();

        mToolbar = findViewById(R.id.toolbarDriverOrder);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTextViewTitle = findViewById(R.id.textViewDriverOrderToolbarTitle);

        // 创建路径对象
        mTrackOverlay = new TrackOverlay(new Handler(Looper.getMainLooper()));
        mTrackOverlay.moveLooper();

        // 开始上传定位
        mQueryLatestPointTask = new QueryLatestPointTask(Constant.GATHER_INTERVAL * 1000, mUserViewModel.getUser().getValue().getUserName(), onTrackListener);
        new Thread(mQueryLatestPointTask).start();

        // 跳转到达上车地点界面
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutDriverOrder, new ReceiveOrderFragment(), null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 返回上一级页面
                FragmentManager fragmentManager = getSupportFragmentManager();
                getSupportFragmentManager().popBackStack();
                if (fragmentManager.getBackStackEntryCount() == 1) {
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
        try {
            // 断开连接
            unbindService(serviceConnection);
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TraceHelper.stopGather();
    }

    /**
     * 设置工具栏标题
     *
     * @param title 标题
     */
    @Override
    public void setTitle(String title) {
        mTextViewTitle.setText(title);
    }

    /**
     * 设置工具栏是否展示
     *
     * @param isShown 是否展示
     */
    @Override
    public void showToolbar(boolean isShown) {
        if (isShown == false) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
    }

    /**
     * 设置工具栏返回按钮是否显示
     *
     * @param isShown 是否展示
     */
    @Override
    public void showBackButton(boolean isShown) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(isShown);
    }

    /**
     * 开启WebSocket服务
     */
    private void startService() {
        Intent intent = new Intent(getApplicationContext(), DriverOrderService.class);
        startService(intent);
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Intent intent = new Intent(getApplicationContext(), DriverOrderService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 注册广播接收器
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constant.FILTER_CONTENT);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(Constant.EXTRA_RESPONSE_MESSAGE);
                Log.d("MESSAGE", message);
            }
        };

        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    // 服务连接监听器
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (DriverOrderService.DriverOrderBinder) service;
            mDriverOrderService = mBinder.getService(mUserViewModel.getUser().getValue().getToken());
            mDriverOrderClient = mDriverOrderService.getClient();
            mMessageHelper.setClient(mDriverOrderClient);
            Toast.makeText(getApplicationContext(), "Service已连接", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "Service已断开", Toast.LENGTH_SHORT).show();
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
            if (status == 0) {
                Toast.makeText(getApplicationContext(), "开启鹰眼服务成功", Toast.LENGTH_SHORT).show();
                TraceHelper.startGather();

            } else {
                Toast.makeText(getApplicationContext(), "开启鹰眼服务失败", Toast.LENGTH_SHORT).show();
            }

        }

        // 停止服务回调
        @Override
        public void onStopTraceCallback(int status, String message) {

        }

        // 开启采集回调
        @Override
        public void onStartGatherCallback(int status, String message) {
            if(status!=0){
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "开始收集", Toast.LENGTH_SHORT).show();
        }

        // 停止采集回调
        @Override
        public void onStopGatherCallback(int status, String message) {
            if (status == 0) {
                // 停止上传任务
                mQueryLatestPointTask.setIsExit(true);
                TraceHelper.stopTrace();
            }
        }

        @Override
        public void onPushCallback(byte messageNo, PushMessage message) {

        }

        @Override
        public void onInitBOSCallback(int i, String s) {

        }
    };

    // 轨迹查询结果监听器
    OnTrackListener onTrackListener = new OnTrackListener() {
        @Override
        public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
        }

        @Override
        public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
            if (latestPointResponse.status != StatusCodes.SUCCESS) {
                return;
            }

            // 封装消息
            com.baidu.trace.model.LatLng location = latestPointResponse.getLatestPoint().getLocation();

            //添加定位点
            if (mTrackOverlay.getBaiduMapView() != null) {
                mTrackOverlay.addLatestPoint(new LatLng(location.getLatitude(), location.getLongitude()));
            }

            //封装消息
            Driver driver = mDriverViewModel.getDriver().getValue();
            driver.setCurrentPoint(new LocationPoint(location));
            UpdateDriverBody body = new UpdateDriverBody();
            body.setMessageId(mMessageHelper.getMessageId());
            body.setStartListening(true);
            body.setServiceType(driver.getServiceType());
            body.setLatitude(location.getLatitude());
            body.setLongitude(location.getLongitude());
            BaseMessage message = mMessageHelper.build(MessageType.UPDATE_REQUEST, body);

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

    /**
     * 获取连接器
     * @return
     */
    public DriverOrderClient getClient() {
        return mDriverOrderClient;
    }

    /**
     * 获取定位覆盖对象
     * @return
     */
    public TrackOverlay getTrackOverlay(){
        return mTrackOverlay;
    }
}