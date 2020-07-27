/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/24
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
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
import com.google.gson.Gson;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.OnToolbarListener;
import com.sosotaxi.driver.common.TTSUtility;
import com.sosotaxi.driver.databinding.FragmentReceiveOrderBinding;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.DriverAnswerOrderBody;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.UpdateDriverBody;
import com.sosotaxi.driver.service.net.DriverOrderClient;
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;

import java.util.List;

/**
 * 接单界面
 */
public class ReceiveOrderFragment extends Fragment {

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
    private FragmentReceiveOrderBinding mBinding;

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
     * 消息帮手对象
     */
    private MessageHelper mMessageHelper;

    public ReceiveOrderFragment() {
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
        // 获取司机ViewModel
        mDriverViewModel=new ViewModelProvider(getActivity()).get(DriverViewModel.class);

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_receive_order, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 不显示地图比例尺及缩放控件
        mBinding.mapViewReceiveOrder.showZoomControls(false);
        // 不显示比例尺
        mBinding.mapViewReceiveOrder.showScaleControl(false);

        //设置点击监听器
        mBinding.buttonDriverOrderReceiveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               DriverAnswerOrderBody body=new DriverAnswerOrderBody();
               body.setTakeOrder(true);
               body.setDriver(mDriverViewModel.getDriverVo().getValue());
               body.setOrder(mOrderViewModel.getOrder().getValue());
               // 构造消息
               BaseMessage message=new BaseMessage(MessageType.DRIVER_ANSWER_ORDER_MESSAGE,body);
               // 发送消息
               mMessageHelper.send(message);

                Toast.makeText(getContext(), R.string.hint_receive_order, Toast.LENGTH_SHORT).show();

                // 跳转到达上车点界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,new ArriveStartingPointFragment(),null);
                fragmentTransaction.commit();
            }
        });

        mBinding.buttonDriverOrderDenyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverAnswerOrderBody body=new DriverAnswerOrderBody();
                body.setTakeOrder(false);
                body.setDriver(mDriverViewModel.getDriverVo().getValue());
                body.setOrder(null);
                // 构造消息
                BaseMessage message=new BaseMessage(MessageType.DRIVER_ANSWER_ORDER_MESSAGE,body);
                // 发送消息
                mMessageHelper.send(message);

                Toast.makeText(getContext(), R.string.hint_deny_order, Toast.LENGTH_SHORT).show();
                // 返回首页
                getActivity().finish();
            }
        });

        // 填充布局
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 获取百度地图对象
        mBaiduMap = mBinding.mapViewReceiveOrder.getMap();

        // 获取路径规划对象
        mSearch=RoutePlanSearch.newInstance();

        // 设置路径规划结果监听器
        mSearch.setOnGetRoutePlanResultListener(onGetRoutePlanResultListener);

        // 规划路径
        initRoutePlan();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof OnToolbarListener) {
            OnToolbarListener onToolbarListener=((OnToolbarListener) getActivity());
            // 改变工具栏标题
            onToolbarListener.setTitle(getString(R.string.title_driver_order_detail));
        }
    }

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
                mBinding.textViewDriverOrderReceiveOrderHint.setText(getString(R.string.hint_estimate_distance)+String.format("%.1f",distance)+getString(R.string.hint_kilometer_estimate)+timeBuffer.toString());
                mTtsUtility.speaking(getString(R.string.hint_receive)+mBinding.textViewDriverReceiveOrderFrom.getText().toString()
                        +getString(R.string.hint_to)+mBinding.textViewDriverOrderReceiveOrderTo.getText().toString()
                        +getString(R.string.hint_order) +mBinding.textViewDriverOrderReceiveOrderHint.getText().toString());
                // 设置数据
                overlay.setData(drivingRouteLine);
                // 在地图上绘制路线
                overlay.addToMap(false);
                // 自动缩放至合适位置
                overlay.zoomToSpanPaddingBounds(100, 100, 100, 100);
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBinding.mapViewReceiveOrder.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBinding.mapViewReceiveOrder.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBinding.mapViewReceiveOrder.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
        if(mBaiduMap!=null){
            mBaiduMap.clear();
        }
    }

    /**
     * 路径规划
     */
    private void initRoutePlan() {
        Order order=mOrderViewModel.getOrder().getValue();
        LocationPoint departPoint=order.getDepartPoint();
        LocationPoint destinationPoint=order.getDestinationPoint();
        // 设置起始点数据
        PlanNode stNode = PlanNode.withLocation(new LatLng(departPoint.getLatitude(),departPoint.getLongitude()));
        PlanNode enNode = PlanNode.withLocation(new LatLng(destinationPoint.getLatitude(),destinationPoint.getLongitude()));
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }
}