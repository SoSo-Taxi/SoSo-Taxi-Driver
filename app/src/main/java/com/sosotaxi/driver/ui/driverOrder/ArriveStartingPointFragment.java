/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.ui.widget.recyclerview.vlayout.extend.ViewLifeCycleHelper;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.entity.DeleteEntityResponse;
import com.baidu.trace.api.entity.EntityInfo;
import com.baidu.trace.api.entity.EntityListRequest;
import com.baidu.trace.api.entity.EntityListResponse;
import com.baidu.trace.api.entity.FilterCondition;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.entity.SearchResponse;
import com.baidu.trace.api.entity.UpdateEntityResponse;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.application.MapApplication;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.OnToolbarListener;
import com.sosotaxi.driver.common.TTSUtility;
import com.sosotaxi.driver.databinding.FragmentArriveStartingPointBinding;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.message.ArriveDepartPointBody;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.UpdateDriverBody;
import com.sosotaxi.driver.service.net.QueryLatestPointTask;
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.ui.overlay.TrackOverlay;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;
import com.sosotaxi.driver.utils.ContactHelper;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.utils.NavigationHelper;
import com.sosotaxi.driver.utils.PermissionHelper;
import com.sosotaxi.driver.utils.TraceHelper;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/**
 * 到达上车地点界面
 */
public class ArriveStartingPointFragment extends Fragment {

    /**
     * 起始点
     */
    private BNRoutePlanNode mStartNode;

    /**
     * 终点
     */
    private BNRoutePlanNode mEndNode;

    /**
     * 路径规划对象
     */
    private RoutePlanSearch mSearch;

    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;

    /**
     * 语音播报对象
     */
    private TTSUtility mTtsUtility;

    /**
     * Application
     */
    private MapApplication mMapApplication;

    /**
     * 处理器
     */
    private Handler mHandler;

    /**
     * 消息帮手
     */
    private MessageHelper mMessageHelper;

    /**
     * 订单ViewModel
     */
    private OrderViewModel mOrderViewModel;

    /**
     * 司机ViewModel
     */
    private DriverViewModel mDriverViewModel;

    /**
     * 数据绑定对象
     */
    private FragmentArriveStartingPointBinding mBinding;

    public ArriveStartingPointFragment() {
        // 获取语音播报对象
        mTtsUtility=TTSUtility.getInstance(getContext());
        // 获取控制器
        mHandler=new Handler(Looper.getMainLooper());
        // 获取消息帮助对象
        mMessageHelper=MessageHelper.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取订单ViewModel
        mOrderViewModel=new ViewModelProvider(getActivity()).get(OrderViewModel.class);
        // 获取司机ViewModel
        mDriverViewModel=new ViewModelProvider(getActivity()).get(DriverViewModel.class);

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_arrive_starting_point, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 显示尾号
        String phone=mOrderViewModel.getOrder().getValue().getPassengerPhoneNumber();
        mBinding.textViewDriverOrderArriveStartingPointNumber.setText(phone.substring(phone.length()-4));

        // 不显示地图比例尺及缩放控件
        mBinding.baiduMapViewDriverOrderArriveStartingPoint.showZoomControls(false);
        mBinding.baiduMapViewDriverOrderArriveStartingPoint.showScaleControl(false);

        // 设置短信按钮监听器
        mBinding.buttonDriverOrderArriveStartingPointText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查权限
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionHelper.hasBaseAuth(getContext(), Manifest.permission.SEND_SMS)==false) {
                        // 未获得则请求权限
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, Constant.PERMISSION_SEND_SMS_REQUEST);
                        return;
                    }
                }
                // 获取乘客联系方式
                String phone=mOrderViewModel.getOrder().getValue().getPassengerPhoneNumber();
                String content=getString(R.string.sms_template_part_one)+mBinding.textViewDriverOrderArriveStartingPointTime+getString(R.string.sms_template_part_two);
                //发送短信
                ContactHelper.sendMessage(getContext(),phone,content);
            }
        });

        // 设置电话按钮监听器
        mBinding.buttonDriverOrderArriveStartingPointPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查权限
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionHelper.hasBaseAuth(getContext(), Manifest.permission.CALL_PHONE)==false) {
                        // 未获得则请求权限
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constant.PERMISSION_CALL_PHONE_REQUEST);
                        return;
                    }
                }
                // 获取乘客联系方式
                String phone=mOrderViewModel.getOrder().getValue().getPassengerPhoneNumber();
                // 拨打电话
                ContactHelper.makeCall(getContext(),phone);
            }
        });

        // 设置滑动按钮监听器
        mBinding.slideButtonArriveStartingPoint.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                // 封装消息
                ArriveDepartPointBody body=new ArriveDepartPointBody();
                body.setOrder(mOrderViewModel.getOrder().getValue());
                BaseMessage message=new BaseMessage(MessageType.ARRIVE_DEPART_POINT_MESSAGE,body);

                //发送消息
                mMessageHelper.send(message);

                Toast.makeText(getContext(), "确认成功!", Toast.LENGTH_SHORT).show();
                // 跳转接到乘客界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,new PickUpPassengerFragment(),null);
                fragmentTransaction.commit();
            }
        });

        // 设置点击监听器
        mBinding.constraintLayoutArriveStartingPointNavigation.setOnClickListener(onClickListener);
        mBinding.imageButtonDriverOrderArriveStartingPointNavigation.setOnClickListener(onClickListener);
        mBinding.textViewDriverOrderArriveStartingPointNavigation.setOnClickListener(onClickListener);

        // 填充布局
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapApplication=(MapApplication) getActivity().getApplicationContext();

        // 获取百度地图对象
        mBaiduMap = mBinding.baiduMapViewDriverOrderArriveStartingPoint.getMap();

        // 获取路径规划对象
        mSearch = RoutePlanSearch.newInstance();

        // 设置路径规划结果监听器
        mSearch.setOnGetRoutePlanResultListener(onGetRoutePlanResultListener);

        // 路径规划
        initRoutePlan();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBinding.baiduMapViewDriverOrderArriveStartingPoint.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBinding.baiduMapViewDriverOrderArriveStartingPoint.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBinding.baiduMapViewDriverOrderArriveStartingPoint.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
        if(mBaiduMap!=null){
            mBaiduMap.clear();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof OnToolbarListener){
            OnToolbarListener onToolbarListener=((OnToolbarListener) getActivity());
            // 改变工具栏标题
            onToolbarListener.setTitle(getString(R.string.title_driver_order_processing));
        }
        if(getActivity() instanceof DriverOrderActivity){
            DriverOrderActivity activity=(DriverOrderActivity)getActivity();
            // 设置连接器
            mMessageHelper.setClient(activity.getClient());
            // 设置地图视图
            activity.getTrackOverlay().setBaiduMapView(mBinding.baiduMapViewDriverOrderArriveStartingPoint);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 获取乘客联系方式
        String phone=mOrderViewModel.getOrder().getValue().getPassengerPhoneNumber();
        String content=getString(R.string.sms_template_part_one)+mBinding.textViewDriverOrderArriveStartingPointTime+getString(R.string.sms_template_part_two);
        switch (requestCode){
            case Constant.PERMISSION_SEND_SMS_REQUEST:
                if(PermissionHelper.hasBaseAuth(getContext(),Manifest.permission.SEND_SMS)==false){
                    // 未获得权限则提示用户权限作用
                    Toast.makeText(getContext(), R.string.hint_permission_send_message_restrict, Toast.LENGTH_SHORT).show();
                    break;
                }
                // 发送短信
                ContactHelper.sendMessage(getContext(),phone,content);
                break;
            case Constant.PERMISSION_CALL_PHONE_REQUEST:
                if(PermissionHelper.hasBaseAuth(getContext(),Manifest.permission.CALL_PHONE)==false){
                    // 未获得权限则提示用户权限作用
                    Toast.makeText(getContext(), R.string.hint_permission_call_restrict, Toast.LENGTH_SHORT).show();
                    break;
                }
                // 拨打电话
                ContactHelper.makeCall(getContext(),phone);
                break;
            case Constant.PERMISSION_NAVIGATION_REQUEST:
                if (PermissionHelper.hasBaseAuth(getContext(),Constant.AUTH_ARRAY_NAVIGATION) == false) {
                    // 未获得权限则提示用户权限作用
                    Toast.makeText(getContext(), R.string.hint_permission_navigation_restrict, Toast.LENGTH_SHORT).show();
                    break;
                }

                // 初始化导航
                NavigationHelper.init();
                if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                    Order order=mOrderViewModel.getOrder().getValue();
                    LocationPoint departPoint=order.getDepartPoint();
                    LocationPoint destinationPoint=order.getDestinationPoint();
                    // 设置起始点数据
                    mStartNode = new BNRoutePlanNode.Builder()
                            .latitude(departPoint.getLatitude())
                            .longitude(departPoint.getLongitude())
                            .name(order.getDepartName())
                            .description(order.getDepartName())
                            .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                            .build();
                    mEndNode = new BNRoutePlanNode.Builder()
                            .latitude(destinationPoint.getLatitude())
                            .longitude(destinationPoint.getLongitude())
                            .name(order.getDestinationName())
                            .description(order.getDestinationName())
                            .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                            .build();

                    NavigationHelper.routePlanToNavigation(getContext(),mStartNode, mEndNode, null);
                }
                break;

        }
    }

    // 点击监听器
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 授权检查
            if (Build.VERSION.SDK_INT >= 23) {
                if (PermissionHelper.hasBaseAuth(getContext(), Constant.AUTH_ARRAY_NAVIGATION) == false) {
                    // 未获得则请求权限
                    requestPermissions(Constant.AUTH_ARRAY_NAVIGATION, Constant.PERMISSION_NAVIGATION_REQUEST);
                    return;
                }
            }

            // 初始化导航
            NavigationHelper.init();
            if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                Order order=mOrderViewModel.getOrder().getValue();
                LocationPoint currentPoint=mDriverViewModel.getDriver().getValue().getCurrentPoint();
                LocationPoint departPoint=order.getDepartPoint();

                // 设置起始点数据
                mEndNode = new BNRoutePlanNode.Builder()
                        .latitude(departPoint.getLatitude())
                        .longitude(departPoint.getLongitude())
                        .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                        .build();
                mStartNode = new BNRoutePlanNode.Builder()
                        .latitude(currentPoint.getLatitude())
                        .longitude(currentPoint.getLongitude())
                        .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                        .build();

                NavigationHelper.routePlanToNavigation(getContext(),mStartNode, mEndNode, null);
            }
        }
    };

    // 路径规划结果监听器
    OnGetRoutePlanResultListener onGetRoutePlanResultListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            //创建DrivingRouteOverlay实例
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
            // 清除原有路线
            overlay.removeFromMap();
            // 获取规划路径集
            List<DrivingRouteLine> routes = drivingRouteResult.getRouteLines();
            if (routes != null && routes.size() > 0) {
                // 获取路径
                DrivingRouteLine drivingRouteLine=drivingRouteResult.getRouteLines().get(0);
                // 计算里程与时间
                double distance=drivingRouteLine.getDistance()/1000.0;
                int hour=drivingRouteLine.getDuration()/3600;
                int minute=drivingRouteLine.getDuration()%3600/60;
                int second=drivingRouteLine.getDuration()%60;
                StringBuffer timeBuffer=new StringBuffer();
                if(hour!=0){
                    timeBuffer.append(hour+getString(R.string.unit_hour));
                }
                if(minute!=0){
                    timeBuffer.append(minute+getString(R.string.unit_minute));
                }
                if(second!=0){
                    timeBuffer.append(second+getString(R.string.unit_second));
                }
                // 设置提示
                mBinding.textViewDriverOrderArriveStartingPointHint.setText(getString(R.string.hint_estimate_distance)+String.format("%.1f",distance)+getString(R.string.hint_kilometer_estimate)+timeBuffer.toString());
                mBinding.textViewDriverOrderArriveStartingPointTime.setText(timeBuffer.toString());
                // 语音播报信息
                mTtsUtility.speaking(getString(R.string.hint_order_has_started)+
                        mBinding.textViewDriverOrderArriveStartingPointDestination.getText().toString() +
                        mBinding.textViewDriverOrderArriveStartingPointHint.getText().toString());
                // 设置数据
                overlay.setData(drivingRouteLine);
                // 在地图上绘制路线
                overlay.addToMap(true);
                // 自动缩放至合适位置
                overlay.zoomToSpanPaddingBounds(100, 100, 100, 400);
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    /**
     * 路径规划
     */
    private void initRoutePlan() {
        Order order=mOrderViewModel.getOrder().getValue();
        Driver driver=mDriverViewModel.getDriver().getValue();
        LocationPoint currentPoint=driver.getCurrentPoint();
        LocationPoint departPoint=order.getDepartPoint();
        // 设置起始点数据
        PlanNode stNode = PlanNode.withLocation(new LatLng(currentPoint.getLatitude(),currentPoint.getLongitude()));
        PlanNode enNode = PlanNode.withLocation(new LatLng(departPoint.getLatitude(),departPoint.getLongitude()));
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }
}