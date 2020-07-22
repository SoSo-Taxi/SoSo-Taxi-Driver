/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
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
import com.sosotaxi.driver.databinding.FragmentReceiveOrderBinding;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.UpdateDriverBody;
import com.sosotaxi.driver.service.net.DriverOrderClient;
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
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

    private DriverOrderClient mDriverOrderClient;

    public ReceiveOrderFragment() {
        // 所需空构造器

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_receive_order, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 不显示地图比例尺及缩放控件
        mBinding.mapViewReceiveOrder.showZoomControls(false);
        // 不显示比例尺
        mBinding.mapViewReceiveOrder.showScaleControl(false);

        //设置滑动监听器
        mBinding.buttonDriverOrderReceiveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverOrderActivity activity=(DriverOrderActivity) getActivity();
                mDriverOrderClient=activity.getClient();
                if(mDriverOrderClient!=null){
                    UpdateDriverBody body=new UpdateDriverBody();
                    body.setMessageId(1);
                    body.setLatitude(40);
                    body.setLongitude(40);
                    body.setDispatched(false);
                    body.setStartListening(true);
                    body.setServerType(1);
                    BaseMessage updateDriverMessage=new BaseMessage(MessageType.UPDATE_REQUEST,body);
                    Gson gson=new Gson();
                    String json=gson.toJson(updateDriverMessage);
                    mDriverOrderClient.send(json);
                }
                Toast.makeText(getContext(), "接单成功!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "拒绝接单!", Toast.LENGTH_SHORT).show();
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
                    timeBuffer.append(hour+"时");
                }
                if(minute!=0){
                    timeBuffer.append(minute+"分");
                }
                if(second!=0){
                    timeBuffer.append(second+"秒");
                }
                // 设置提示
                mBinding.textViewDriverOrderReceiveOrderHint.setText("行程"+String.format("%.1f",distance)+"公里  预计"+timeBuffer.toString());
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
        // TODO: 与订单对接获取起始点
        // 测试用数据
        Toast.makeText(getContext(), R.string.hint_route_planning, Toast.LENGTH_SHORT).show();
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "奥体中心");
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "天安门广场");
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }
}