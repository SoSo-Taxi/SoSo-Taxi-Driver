/**
 * @Author 屠天宇
 * @CreateTime 2020/7/14
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.ui.home;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.google.gson.Gson;
import com.sosotaxi.driver.R;

import com.sosotaxi.driver.adapter.UndoneOrderRecycleViewAdapter;
import com.sosotaxi.driver.common.CircleProgressBar;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.OnToolbarListener;
import com.sosotaxi.driver.common.ProgressRunnable;
import com.sosotaxi.driver.common.TTSUtility;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.User;
import com.sosotaxi.driver.model.message.AskForDriverBody;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.UpdateDriverBody;
import com.sosotaxi.driver.service.net.DriverStatisticsTask;
import com.sosotaxi.driver.service.net.QueryDriverTask;
import com.sosotaxi.driver.ui.driverOrder.DriverOrderActivity;
import com.sosotaxi.driver.utils.ContactHelper;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.utils.NavigationHelper;
import com.sosotaxi.driver.utils.PermissionHelper;
import com.sosotaxi.driver.utils.TraceHelper;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;
import com.sosotaxi.driver.viewModel.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private UserViewModel mUserViewModel;
    private OrderViewModel mOrderViewModel;
    private DriverViewModel mDriverViewModel;
    private MessageHelper mMessageHelper;

    //司机评分
    private TextView mServicePointTextView;
    //司机流水
    private TextView mAccountTextView;
    //司机流水统计值
    private double mAccount = 0;
    //司机接单数
    private TextView mReceivingOrderQuantityTextView;
    //司机接单数统计值
    private int mReceivingOrderQuantity = 0;
    //司机在线时长
    private TextView mOnlineTimeTextView;
    private CalculateTimeRunnable mCalculateTimeThread;
    //统计在线时长 单位：秒
    private long startTime = 0;
    private long endTime = 0;
    //修改在线时长handler
    private Handler mSetOnlineTimeHandler;
    //未完成订单数目
    private TextView mUndoneOrderQuantityTextView;
    private RecyclerView mUndoneOrderRecycleView;
    private UndoneOrderRecycleViewAdapter mUndoneOrderRecycleViewAdapter;


    private List<String> startingPoints = new ArrayList<String>(Arrays.asList("人民艺术剧院", "天坛公园", "世贸天阶", " 三里屯"));
    private List<String> destinations = new ArrayList<String>(Arrays.asList("天安门广场", "八宝山", "圆明园", "故宫"));
    private List<String> scheduleTime = new ArrayList<String>(Arrays.asList("2020年7月26日15：00", "2020年7月26日20：00", "2020年7月27日7：00", "2020年7月27日12：00"));

    //听单动态效果
    private CircleProgressBar mCircleProgressBar;
    private TextView mStartOrderTextView;
    private Thread mDrawingCircleThread;
    private ProgressRunnable mProgressRunnable;


    private TextView mEndWorkTextView;

    private TTSUtility mTtsUtility;

    private boolean mToggle;


    public HomeFragment() {
        // 获取消息帮手
        mMessageHelper = MessageHelper.getInstance();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mServicePointTextView = root.findViewById(R.id.firstpage_servicepoint_textView);
        //新用户默认80服务分
        mServicePointTextView.setText("80");
        mAccountTextView = root.findViewById(R.id.firstpage_account_textView);
        mAccountTextView.setText("0.00");
        mReceivingOrderQuantityTextView = root.findViewById(R.id.firstpage_orderquantity_textView);
        mReceivingOrderQuantityTextView.setText("0");
        mTtsUtility = TTSUtility.getInstance(getActivity().getApplicationContext());
        //连接一下，让后面的语音延迟小一些
        mTtsUtility.speaking(getString(R.string.slogan));


        mOnlineTimeTextView = root.findViewById(R.id.firstpage_onlinetime_textView);

        mUndoneOrderQuantityTextView = root.findViewById(R.id.firstpage_undone_orderquantity_textView);

        mUndoneOrderRecycleView = root.findViewById(R.id.first_page_undone_order_recycleView);
        mUndoneOrderRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUndoneOrderRecycleViewAdapter = new UndoneOrderRecycleViewAdapter(getContext(), startingPoints, destinations, scheduleTime);

        mUndoneOrderRecycleView.setAdapter(mUndoneOrderRecycleViewAdapter);

        mUndoneOrderQuantityTextView.setText(String.valueOf(mUndoneOrderRecycleViewAdapter.getItemCount()));


        mEndWorkTextView = root.findViewById(R.id.end_work_textView);

        setEndWorkTextViewVisible(false);
        mCircleProgressBar = root.findViewById(R.id.circle_progress_bar);
        mStartOrderTextView = root.findViewById(R.id.start_order_textView);
        mProgressRunnable = new ProgressRunnable(mCircleProgressBar);
        mDrawingCircleThread = new Thread(mProgressRunnable);

        mToggle = true;

        // 开始听单听单按钮
        mStartOrderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查权限
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionHelper.hasBaseAuth(getContext(), Constant.AUTH_ARRAY_TRACK)==false) {
                        // 未获得则请求权限
                        requestPermissions(Constant.AUTH_ARRAY_TRACK, Constant.PERMISSION_TRACK_REQUEST);
                        return;
                    }
                }
                toggleListening();
            }
        });

        //收车代表结束一天行程，这时将统计数据返回服务器
        mEndWorkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHearingOrderStartState();
                // 停止记录轨迹
                TraceHelper.stopGather();

                postDriverStatistics();
                //一天结束后，相关统计量归零
                startTime = 0;
                endTime = 0;
                mReceivingOrderQuantity = 0;
                mAccount = 0;
                mTtsUtility.speaking("已收车，结束一天工作");

            }
        });


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            System.out.println("onActivityResult");
            if (resultCode == RESULT_OK) {
                System.out.println("RESULT_OK");
                switch (requestCode) {
                    case Constant.ASK_AMOUNT_REQUEST:
                        // 获取订单金额
                        System.out.println("ASK_AMOUNT_REQUEST");
                        Bundle bundle = data.getExtras();
                        double total = bundle.getDouble(Constant.EXTRA_TOTAL);
                        System.out.println(total + "total");
                        // TODO: 处理订单金额数据
                        setAccountTextView(total);
                        mReceivingOrderQuantity++;
                        mReceivingOrderQuantityTextView.setText(mReceivingOrderQuantity);
                        Log.d("TOTAL", String.valueOf(total));
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println(resultCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constant.PERMISSION_TRACK_REQUEST:
                if(PermissionHelper.hasBaseAuth(getContext(),Constant.AUTH_ARRAY_TRACK)==false){
                    // 未获得权限则提示用户权限作用
                    Toast.makeText(getContext(), R.string.hint_permission_track, Toast.LENGTH_SHORT).show();
                    break;
                }

                toggleListening();
                break;
        }
    }

    //向服务器传统计数据
    private void postDriverStatistics() {
        Driver driver = new Driver();
        User user = mUserViewModel.getUser().getValue();
        if (user.getUserId() == null) {
            driver.setUserId((long) 18);
        } else {
            driver.setUserId(user.getUserId());
        }
        driver.setAccountFlow(mAccount);
        driver.setWorkSeconds((int) endTime);
        driver.setOrderNum(mReceivingOrderQuantity);
        new Thread(new DriverStatisticsTask(driver, mFillTextHandler)).start();
    }

    private void setAccountTextView(double total) {
        mAccount += total;
        BigDecimal bigDecimal = new BigDecimal(mAccount);
        double result;
        //最多五位有效数字
        if (mAccount >= 10000) {
            result = 9999.9;
        } else if (mAccount >= 1000) {
            result = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            result = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        mAccountTextView.setText(String.valueOf(result));
    }

    private void setOnlineTimeTextView() {
        long day = startTime / (24 * 60 * 60);
        long hour = startTime / (60 * 60);
        long min = startTime / 60;
        long s = startTime;
        if (hour > 0) {
            float m = (float) ((min - hour * 60) / 60.0);
            float h = ((float) (Math.round((hour + m) * 10))) / 10;
            mOnlineTimeTextView.setText(h + "时");
        } else if (min > 0) {
            mOnlineTimeTextView.setText(min + "分钟");
        } else if (s > 0) {
            mOnlineTimeTextView.setText(s + "秒");
        } else {
            mOnlineTimeTextView.setText(0 + "时");
        }
    }

    //更新代办行程
    private void updateUndoneOrder(String startingPoint, String destination, String schedule) {
        startingPoints.add(startingPoint);
        destinations.add(destination);
        scheduleTime.add(schedule);
        mUndoneOrderRecycleViewAdapter = new UndoneOrderRecycleViewAdapter(getContext(), startingPoints, destinations, scheduleTime);
        mUndoneOrderRecycleView.setAdapter(mUndoneOrderRecycleViewAdapter);
        mUndoneOrderQuantityTextView.setText(String.valueOf(mUndoneOrderRecycleViewAdapter.getItemCount()));
    }

    private void setHearingOrderStartState() {
        mStartOrderTextView.setText("开始听单");
        setEndWorkTextViewVisible(false);
        mProgressRunnable.setStop(true);
        mProgressRunnable = new ProgressRunnable(mCircleProgressBar);
        mDrawingCircleThread = new Thread(mProgressRunnable);
        //结束计时，获取在线时长
        mCalculateTimeThread.setStop(true);
        endTime = startTime - endTime;
        Toast.makeText(getContext().getApplicationContext(), "本次在线时间" + endTime + "秒", Toast.LENGTH_LONG).show();
    }

    private void setEndWorkTextViewVisible(boolean toggle) {
        if (toggle) {
            mEndWorkTextView.setVisibility(View.VISIBLE);
        } else {
            mEndWorkTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // 获取用户ViewModel
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        // 获取订单ViewModel
        mOrderViewModel = new ViewModelProvider(getActivity()).get(OrderViewModel.class);
        // 获取司机ViewModel
        mDriverViewModel = new ViewModelProvider(getActivity()).get(DriverViewModel.class);

        Driver driver = new Driver();
        DriverVo driverVo = new DriverVo();
        driver.setUserName(mUserViewModel.getUser().getValue().getUserName());
        new Thread(new QueryDriverTask(driver, handler)).start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            if (bundle.getString(Constant.EXTRA_ERROR) != null) {
                Toast.makeText(getContext(), bundle.getString(Constant.EXTRA_ERROR), Toast.LENGTH_SHORT).show();
                return false;
            }

            boolean isSuccessful = bundle.getBoolean(Constant.EXTRA_IS_SUCCESSFUL);

            if (isSuccessful) {
                // 设置司机信息
                Driver driver = bundle.getParcelable(Constant.EXTRA_DRIVER);
                DriverVo driverVo = bundle.getParcelable(Constant.EXTRA_DRIVER_VO);
                mDriverViewModel.getDriver().setValue(driver);
                mDriverViewModel.getDriverVo().setValue(driverVo);
            }
            return false;
        }
    });


    //统计在线时间
    class CalculateTimeRunnable extends Thread {
        private boolean isStop = false;

        public void setStop(boolean stop) {
            isStop = stop;
        }

        @Override
        public void run() {
            while (!isStop) {
                mSetOnlineTimeHandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void toggleListening(){
        // 开始听单
        if (mToggle || mStartOrderTextView.getText() == "开始听单") {
            // 开始记录轨迹
            TraceHelper.startTrace();

            mStartOrderTextView.setText("听单中");
            mTtsUtility.speaking("正在为您接受附近的订单");//为您接受附近的订单


            //开始计时
            mSetOnlineTimeHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    startTime++;
                    setOnlineTimeTextView();
                    return true;
                }
            });
            mCalculateTimeThread = new CalculateTimeRunnable();
            mCalculateTimeThread.start();

            setEndWorkTextViewVisible(true);
            mDrawingCircleThread.start();
            mToggle = false;
        } else {
            // 停止听单
            // 停止记录轨迹
            TraceHelper.stopGather();

            TTSUtility.getInstance(getActivity().getApplicationContext()).speaking("停止听单");
            setHearingOrderStartState();
            mToggle = true;
        }
    }


    private Handler mFillTextHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int servicePoint = bundle.getInt("servicePoint");
            if (servicePoint != -1) {
                mServicePointTextView.setText(String.valueOf(servicePoint));
            }
            return true;
        }
    });
}