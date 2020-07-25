/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
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
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.TTSUtility;
import com.sosotaxi.driver.databinding.FragmentArriveDestinationBinding;
import com.sosotaxi.driver.databinding.FragmentArriveStartingPointBinding;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.message.ArriveDestPointBody;
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;
import com.sosotaxi.driver.utils.ContactHelper;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.utils.NavigationHelper;
import com.sosotaxi.driver.utils.PermissionHelper;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;

import java.util.List;

/**
 * 到达目的地界面
 */
public class ArriveDestinationFragment extends Fragment {

    /**
     * 起始节点
     */
    private BNRoutePlanNode mStartNode;

    /**
     * 终点
     */
    private BNRoutePlanNode mEndNode;

    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;

    /**
     * 路径规划对象
     */
    private RoutePlanSearch mSearch;

    /**
     * 语音播报对象
     */
    private TTSUtility mTtsUtility;

    /**
     * 订单ViewModel
     */
    private OrderViewModel mOrderViewModel;

    /**
     * 数据绑定对象
     */
    private FragmentArriveDestinationBinding mBinding;

    /**
     * 消息帮手对象
     */
    private MessageHelper mMessageHelper;

    public ArriveDestinationFragment() {
        // 获取语音播报对象
        mTtsUtility=TTSUtility.getInstance(getContext());
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

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_arrive_destination, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 不显示地图比例尺及缩放控件
        mBinding.baiduMapViewDriverArriveDestination.showZoomControls(false);
        // 不显示比例尺
        mBinding.baiduMapViewDriverArriveDestination.showScaleControl(false);

        // 设置点击监听器
        mBinding.constraintLayoutArriveDestinationNavigation.setOnClickListener(onClickListener);
        mBinding.imageButtonDriverArriveDestinationNavigation.setOnClickListener(onClickListener);
        mBinding.textViewDriverOrderArriveDestinationNavigation.setOnClickListener(onClickListener);

        // 设置滑动监听器
        mBinding.slideButtonArriveDestination.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                // 发送消息
                Toast.makeText(getContext(), R.string.hint_confirm_successful, Toast.LENGTH_SHORT).show();

                // 跳转确认账单界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,new ConfirmBillFragment(),null);
                fragmentTransaction.commit();
            }
        });

        // 填充布局
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 获取百度地图对象
        mBaiduMap = mBinding.baiduMapViewDriverArriveDestination.getMap();

        // 获取路径规划对象
        mSearch = RoutePlanSearch.newInstance();

        // 设置路径规划结果监听器
        mSearch.setOnGetRoutePlanResultListener(listener);

        // 导航初始化
        NavigationHelper.init();

        // 路径规划
        initRoutePlan();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBinding.baiduMapViewDriverArriveDestination.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBinding.baiduMapViewDriverArriveDestination.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBinding.baiduMapViewDriverArriveDestination.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
        if(mBaiduMap!=null){
            mBaiduMap.clear();
        }
    }

    // 请求权限结果处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constant.PERMISSION_NAVIGATION_REQUEST:
                if (PermissionHelper.hasBaseAuth(getContext(),Constant.AUTH_ARRAY_NAVIGATION) == false) {
                    Toast.makeText(getContext(), R.string.hint_permission_navigation_restrict, Toast.LENGTH_SHORT).show();
                    break;
                }
                NavigationHelper.routePlanToNavigation(getContext(),mStartNode, mEndNode, null);
                break;

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof DriverOrderActivity){
            DriverOrderActivity activity=(DriverOrderActivity)getActivity();
            // 设置地图视图
            activity.getTrackOverlay().setBaiduMapView(mBinding.baiduMapViewDriverArriveDestination);
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
                    requestPermissions(Constant.AUTH_ARRAY_NAVIGATION, Constant.PERMISSION_SEND_SMS_REQUEST);
                    return;
                }

            }

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
        }
    };

    // 路径规划结果监听器
    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
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
            //创建DrivingRouteOverlay对象
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
                mBinding.textViewDriverArriveDestinationHint.setText(getString(R.string.hint_estimate_distance)+String.format("%.1f",distance)+getString(R.string.hint_kilometer_estimate)+timeBuffer.toString());
                // 语音播报信息
                mTtsUtility.speaking("已接到乘客，请前往目的地"+
                        mBinding.textViewDriverArriveDestinationDestination.getText().toString() +
                        mBinding.textViewDriverArriveDestinationHint.getText().toString());
                // 设置数据
                overlay.setData(drivingRouteLine);
                // 在地图上绘制路线
                overlay.addToMap(false);
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
        Toast.makeText(getContext(), R.string.hint_route_planning, Toast.LENGTH_SHORT).show();
        Order order=mOrderViewModel.getOrder().getValue();
        LocationPoint departPoint=order.getDepartPoint();
        LocationPoint destinationPoint=order.getDestinationPoint();
        // 设置起始点数据
        PlanNode stNode = PlanNode.withLocation(new LatLng(departPoint.getLatitude(),departPoint.getLongitude()));
        PlanNode enNode = PlanNode.withLocation(new LatLng(destinationPoint.getLatitude(),destinationPoint.getLongitude()));
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }
}