/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/25
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
import com.sosotaxi.driver.databinding.FragmentReserveOrderBinding;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.UpdateDriverBody;
import com.sosotaxi.driver.service.net.DriverOrderClient;
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;
import com.sosotaxi.driver.viewModel.UserViewModel;

import java.util.List;

/**
 * 预定订单界面
 */
public class ReserveOrderFragment extends Fragment {

    /**
     * 数据绑定
     */
    private FragmentReserveOrderBinding mBinding;

    /**
     * 订单ViewModel
     */
    private OrderViewModel mOrderViewModel;

    /**
     * 司机ViewModel
     */
    private DriverViewModel mDriverViewModel;

    /**
     * 司机订单连接器
     */
    private DriverOrderClient mDriverOrderClient;

    /**
     * 消息帮手
     */
    private MessageHelper mMessageHelper;

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


    public ReserveOrderFragment() {
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

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_reserve_order, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 不显示地图比例尺及缩放控件
        mBinding.mapViewReserveOrder.showZoomControls(false);
        // 不显示比例尺
        mBinding.mapViewReserveOrder.showScaleControl(false);

        // 设置点击监听器
        mBinding.buttonDriverOrderReceiveReserveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverOrderActivity activity=(DriverOrderActivity) getActivity();
                mDriverOrderClient=activity.getClient();
                if(mDriverOrderClient!=null){

                }
                Toast.makeText(getContext(), R.string.hint_reserve_successful, Toast.LENGTH_SHORT).show();
                // 返回首页
                getActivity().finish();
            }
        });

        // 设置点击监听器
        mBinding.buttonDriverOrderDenyReserveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), R.string.hint_deny_reserve_order, Toast.LENGTH_SHORT).show();
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
        mBaiduMap = mBinding.mapViewReserveOrder.getMap();

        // 获取路径规划对象
        mSearch= RoutePlanSearch.newInstance();

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
            onToolbarListener.setTitle(getString(R.string.title_toolbar_reserve_order) );
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
                    timeBuffer.append(hour+"时");
                }
                if(minute!=0){
                    timeBuffer.append(minute+"分");
                }
                if(second!=0){
                    timeBuffer.append(second+"秒");
                }
                // 设置提示
                mBinding.textViewDriverOrderReserveOrderHint.setText("预计行程"+String.format("%.1f",distance)+"公里  预计"+timeBuffer.toString());
                mTtsUtility.speaking("已为您接到从"+mBinding.textViewDriverReserveOrderFrom.getText().toString()
                        + "到"+mBinding.textViewDriverOrderReserveOrderTo.getText().toString() +"的订单，预约时间"
                        + mBinding.textViewReserveOrderDate.getText().toString()
                        + mBinding.textViewReserveOrderTime.getText().toString()
                        + mBinding.textViewDriverOrderReserveOrderHint.getText().toString());
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
        mBinding.mapViewReserveOrder.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBinding.mapViewReserveOrder.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBinding.mapViewReserveOrder.onDestroy();
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