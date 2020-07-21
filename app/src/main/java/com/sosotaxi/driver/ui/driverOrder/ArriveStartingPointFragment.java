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
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.ui.overlay.TrackOverlay;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;
import com.sosotaxi.driver.utils.ContactHelper;
import com.sosotaxi.driver.utils.NavigationHelper;
import com.sosotaxi.driver.utils.PermissionHelper;
import com.sosotaxi.driver.utils.TraceHelper;
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
     * 路径
     */
    private List<LatLng> latlngs;

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

    private MapApplication mMapApplication;

    private Handler mHandler;

    /**
     * 订单ViewModel
     */
    private OrderViewModel mOrderViewModel;

    /**
     * 数据绑定对象
     */
    private FragmentArriveStartingPointBinding mBinding;

    public ArriveStartingPointFragment() {
        // 初始化路径
        latlngs= new LinkedList<LatLng>();
        // 获取语音播报对象
        mTtsUtility=TTSUtility.getInstance(getContext());

        mHandler=new Handler(Looper.getMainLooper());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取订单ViewModel
        mOrderViewModel=new ViewModelProvider(getActivity()).get(OrderViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_arrive_starting_point, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

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
                // TODO:与订单对接获取乘客联系方式
                // 测试用数据
                String phone="+86 10086";
                String content="您好，我已接单，预计在5分01秒内到达上车点，请做好上车准备。";
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
                // TODO:与订单对接获取乘客联系方式
                // 测试用数据
                String phone="+86 10086";
                // 拨打电话
                ContactHelper.makeCall(getContext(),phone);
            }
        });

        // 设置滑动按钮监听器
        mBinding.slideButtonArriveStartingPoint.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
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
        // 初始化轨迹记录
        TraceHelper.initTrace("8613823831820",2,15,onTraceListener);
        // 开始记录轨迹
        //TraceHelper.startTrace();

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
        TraceHelper.stopGather();
        TraceHelper.stopTrace();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof OnToolbarListener){
            OnToolbarListener onToolbarListener=((OnToolbarListener) getActivity());
            // 改变工具栏标题
            onToolbarListener.setTitle(getString(R.string.title_driver_order_processing));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO:与订单对接获取乘客联系方式
        // 测试用数据
        String phone="+86 10086";
        String content="您好，我已接单，预计在5分01秒内到达上车点，请做好上车准备。";
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
                    // TODO: 与订单对接获取起始点
                    // 测试用数据
                    mStartNode = new BNRoutePlanNode.Builder()
                            .latitude(40.05087)
                            .longitude(116.30142)
                            .name("西二旗地铁站")
                            .description("西二旗地铁站")
                            .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                            .build();
                    mEndNode = new BNRoutePlanNode.Builder()
                            .latitude(39.98340)
                            .longitude(116.42532)
                            .name("奥体中心")
                            .description("奥体中心")
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
                // TODO: 与订单对接获取起始点
                // 测试用数据
                mStartNode = new BNRoutePlanNode.Builder()
                        .latitude(40.05087)
                        .longitude(116.30142)
                        .name("西二旗地铁站")
                        .description("西二旗地铁站")
                        .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                        .build();
                mEndNode = new BNRoutePlanNode.Builder()
                        .latitude(39.98340)
                        .longitude(116.42532)
                        .name("奥体中心")
                        .description("奥体中心")
                        .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                        .build();

                NavigationHelper.routePlanToNavigation(getContext(),mStartNode, mEndNode, null);
            }
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
                Toast.makeText(getContext(), "开启鹰眼服务成功", Toast.LENGTH_SHORT).show();
                TraceHelper.startGather();

            }else{
                Toast.makeText(getContext(), "开启鹰眼服务失败", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "开始收集", Toast.LENGTH_SHORT).show();
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
            // 判断是否响应成功
            if(historyTrackResponse.getStatus()!= StatusCodes.SUCCESS){
                // 失败打印原因
                Toast.makeText(getContext(),historyTrackResponse.getMessage(),Toast.LENGTH_SHORT).show();
                return;
            }
            // 获取轨迹
            List<TrackPoint> trackPoints=historyTrackResponse.trackPoints;
            if(trackPoints==null||trackPoints.size()==0){
                return;
            }

            latlngs.clear();
            for(TrackPoint trackPoint : trackPoints){
                com.baidu.trace.model.LatLng location=trackPoint.getLocation();
                double latitude=location.getLatitude();
                double longitude=location.getLongitude();
                latlngs.add(new LatLng(latitude,longitude));
            }

            // 绘制轨迹
            TrackOverlay trackOverlay=new TrackOverlay();
            trackOverlay.setBaiduMapView(mBinding.baiduMapViewDriverOrderArriveStartingPoint);
            trackOverlay.setLatlngs(latlngs);
            trackOverlay.setHandler(mHandler);
            trackOverlay.drawPolyLine();
            trackOverlay.moveLooper();
        }

        @Override
        public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
            int status=latestPointResponse.status;
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
                    timeBuffer.append(hour+"时");
                }
                if(minute!=0){
                    timeBuffer.append(minute+"分");
                }
                if(second!=0){
                    timeBuffer.append(second+"秒");
                }
                // 设置提示
                mBinding.textViewDriverOrderArriveStartingPointHint.setText("行程"+String.format("%.1f",distance)+"公里  预计"+timeBuffer.toString());
                mBinding.textViewDriverOrderArriveStartingPointTime.setText(timeBuffer.toString());
                // 语音播报信息
                mTtsUtility.speaking("订单已开始，请前往上车点 奥体中心。"+mBinding.textViewDriverOrderArriveStartingPointHint.getText().toString());
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

    // 实体监听器
    OnEntityListener entityListener = new OnEntityListener() {
        @Override
        public void onUpdateEntityCallback(UpdateEntityResponse updateEntityResponse) {
            super.onUpdateEntityCallback(updateEntityResponse);
        }

        @Override
        public void onDeleteEntityCallback(DeleteEntityResponse deleteEntityResponse) {
            super.onDeleteEntityCallback(deleteEntityResponse);
        }

        @Override
        public void onEntityListCallback(EntityListResponse entityListResponse) {
            // 判断是否响应成功
            if(entityListResponse.getStatus()!= StatusCodes.SUCCESS){
                // 失败打印原因
                Toast.makeText(getContext(),entityListResponse.getMessage(),Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取定位
            List<EntityInfo> entityInfos=entityListResponse.getEntities();
            if(entityInfos==null||entityInfos.size()==0){
                return;
            }
            // 清除原路定位息
            latlngs.clear();
            // 获取定位信息
            for(EntityInfo entityInfo : entityInfos){
                com.baidu.trace.model.LatLng location=entityInfo.getLatestLocation().getLocation();
                double latitude=location.getLatitude();
                double longitude=location.getLongitude();
                // TODO: 发送定位给服务端

                latlngs.add(new LatLng(latitude,longitude));
            }
            // 绘制历史轨迹
            TrackOverlay trackOverlay=new TrackOverlay();
            trackOverlay.setBaiduMapView(mBinding.baiduMapViewDriverOrderArriveStartingPoint);
            trackOverlay.setLatlngs(latlngs);
            trackOverlay.setHandler(mHandler);
            trackOverlay.drawPolyLine();
            trackOverlay.moveLooper();
        }

        @Override
        public void onSearchEntityCallback(SearchResponse searchResponse) {
            super.onSearchEntityCallback(searchResponse);
        }

        @Override
        public void onReceiveLocation(TraceLocation traceLocation) {
            super.onReceiveLocation(traceLocation);
        }
    };

    /**
     * 路径规划
     */
    private void initRoutePlan() {
        // TODO: 与订单对接获取起始点
        // TODO: 定位模块获取当前位置
        // 测试用数据
        Toast.makeText(getContext(), R.string.hint_route_planning, Toast.LENGTH_SHORT).show();
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "西二旗地铁站");
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "奥体中心");
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }
}