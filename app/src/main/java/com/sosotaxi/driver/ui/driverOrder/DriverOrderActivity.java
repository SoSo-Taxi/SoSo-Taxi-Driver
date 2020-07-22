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

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.OnToolbarListener;
import com.sosotaxi.driver.service.net.DriverOrderClient;
import com.sosotaxi.driver.service.net.DriverOrderService;
import com.sosotaxi.driver.service.net.OrderMessageReceiver;
import com.sosotaxi.driver.viewModel.OrderViewModel;
import com.sosotaxi.driver.viewModel.UserViewModel;

public class DriverOrderActivity extends AppCompatActivity implements OnToolbarListener {

    private OrderViewModel mOrderViewModel;

    private UserViewModel mUserViewModel;

    private Toolbar mToolbar;
    private TextView mTextViewTitle;

    private DriverOrderClient mDriverOrderClient;
    private DriverOrderService mDriverOrderService;
    private DriverOrderService.DriverOrderBinder mBinder;
    private OrderMessageReceiver mOrderMessageReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);

        mOrderViewModel=new ViewModelProvider(this).get(OrderViewModel.class);
        mUserViewModel=new ViewModelProvider(this).get(UserViewModel.class);

        startService();
        bindService();
        registerReceiver();

        mToolbar = findViewById(R.id.toolbarDriverOrder);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mTextViewTitle=findViewById(R.id.textViewDriverOrderToolbarTitle);

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
            Toast.makeText(getApplicationContext(),"Service已连接",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(),"Service已断开",Toast.LENGTH_SHORT).show();
        }
    };

    public DriverOrderClient getClient(){
        return mDriverOrderClient;
    }
}