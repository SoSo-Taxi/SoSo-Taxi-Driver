package com.sosotaxi.driver.ui.main;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.User;
import com.sosotaxi.driver.model.message.AskForDriverBody;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.UpdateDriverBody;
import com.sosotaxi.driver.service.net.DriverOrderClient;
import com.sosotaxi.driver.service.net.DriverOrderService;
import com.sosotaxi.driver.service.net.OrderMessageReceiver;
import com.sosotaxi.driver.service.net.QueryLatestPointTask;
import com.sosotaxi.driver.ui.driverOrder.DriverOrderActivity;
import com.sosotaxi.driver.ui.userInformation.order.OrderActivity;
import com.sosotaxi.driver.ui.userInformation.personData.PersonalDataActivity;
import com.sosotaxi.driver.ui.userInformation.wallet.WalletActivity;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.utils.TraceHelper;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;
import com.sosotaxi.driver.viewModel.UserViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout drawerLayout;

    private UserViewModel mUserViewModel;

    private DriverViewModel mDriverViewModel;

    private OrderViewModel mOrderViewModel;

    private DriverOrderClient mDriverOrderClient;
    private DriverOrderService mDriverOrderService;
    private DriverOrderService.DriverOrderBinder mBinder;
    private OrderMessageReceiver mOrderMessageReceiver;
    private MessageHelper mMessageHelper;

    private Thread mQueryThread;

    /**
     * 侧边栏用户姓名和
     * 用户其他信息（确定填写内容后更改）
     */
    private TextView mUserName;
    private TextView mUserOtherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mOrderViewModel=new ViewModelProvider(this).get(OrderViewModel.class);

        mUserViewModel=new ViewModelProvider(this).get(UserViewModel.class);

        mDriverViewModel=new ViewModelProvider(this).get(DriverViewModel.class);

        mMessageHelper=MessageHelper.getInstance();

        // 绑定服务
        startService();
        bindService();
        registerReceiver();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 初始化轨迹记录
        TraceHelper.initTrace(mUserViewModel.getUser().getValue().getUserName(),Constant.GATHER_INTERVAL,Constant.PACK_INTERVAL,onTraceListener);
        // 开始记录轨迹
        //TraceHelper.startTrace();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //与登录界面对接后修改
        final View headerView = navigationView.getHeaderView(0);
        mUserName = headerView.findViewById(R.id.username);
        mUserOtherInfo = headerView.findViewById(R.id.user_otherInfo);
        final ImageView headImageView = headerView.findViewById(R.id.head_imageView);

        User user=mUserViewModel.getUser().getValue();
        mUserName.setText(user.getUserName());
        mUserOtherInfo.setText("会员状态");
        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PersonalDataActivity.class);
                headImageView.setDrawingCacheEnabled(true);
                intent.putExtra("image",headImageView.getDrawingCache());
                intent.putExtra("phone",mUserName.getText());
                startActivityForResult(intent,200);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.frameLayoutHome);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Intent intent = null;
        switch (menuItem.getItemId()){
            case R.id.nav_order:
                intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_safety:

            case R.id.nav_wallet:
                intent = new Intent(getApplicationContext(), WalletActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_service:

            case R.id.nav_setting:

                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case Constant.ASK_AMOUNT_REQUEST:
                    // 获取订单金额
                    double amount=data.getDoubleExtra(Constant.EXTRA_TOTAL,0.0);
                    // TODO: 处理订单金额数据
                    break;
                default:
                    break;
            }
        }
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
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 解析消息
                String json=intent.getStringExtra(Constant.EXTRA_RESPONSE_MESSAGE);
                Gson gson=new Gson();
                BaseMessage message=gson.fromJson(json,BaseMessage.class);
                Log.d("MESSAGE",json);
                if(message.getType()== MessageType.ASK_FOR_DRIVER_MESSAGE){
                    AskForDriverBody body =(AskForDriverBody) message.getBody();
                    String city=body.getCity();
                    String phone=body.getPassengerPhoneNumber();
                    Order order=body.getOrder();
                    order.setPassengerPhoneNumber(phone);
                    // 设置订单
                    mOrderViewModel.getOrder().setValue(order);
                    Intent orderIntent = new Intent(getApplicationContext(), DriverOrderActivity.class);
                    startActivityForResult(orderIntent,Constant.ASK_AMOUNT_REQUEST);
                }
            }
        },intentFilter);
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
            if(latestPointResponse.status!= StatusCodes.SUCCESS){
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

    /**
     * 获取连接器
     * @return 连接器
     */
    public DriverOrderClient getClient(){
        return mDriverOrderClient;
    }
}