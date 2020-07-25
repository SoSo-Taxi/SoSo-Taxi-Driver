/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.telephony.SmsManager;
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
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.TTSUtility;
import com.sosotaxi.driver.databinding.FragmentArriveStartingPointBinding;
import com.sosotaxi.driver.databinding.FragmentPickUpPassengerBinding;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.message.ArriveDepartPointBody;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.model.message.PickUpPassengerBody;
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;
import com.sosotaxi.driver.utils.ContactHelper;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.utils.PermissionHelper;
import com.sosotaxi.driver.viewModel.OrderViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 接到乘客界面
 */
public class PickUpPassengerFragment extends Fragment {

    /**
     * 订单ViewModel
     */
    private OrderViewModel mOrderViewModel;

    /**
     * 数据绑定对象
     */
    private FragmentPickUpPassengerBinding mBinding;

    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;

    /**
     * 语音播报对象
     */
    private TTSUtility mTtsUtility;

    /**
     * 路径规划对象
     */
    private RoutePlanSearch mSearch;

    /**
     * 消息帮手对象
     */
    private MessageHelper mMessageHelper;

    public PickUpPassengerFragment() {
        // 获取语音播报对象
        mTtsUtility=TTSUtility.getInstance(getContext());
        // 获取消息帮助对象
        mMessageHelper= MessageHelper.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mOrderViewModel=new ViewModelProvider(getActivity()).get(OrderViewModel.class);

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_pick_up_passenger, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 显示尾号
        String phone=mOrderViewModel.getOrder().getValue().getPassengerPhoneNumber();
        mBinding.textViewDriverPickUpPassengerNumber.setText(phone.substring(phone.length()-4));

        // 不显示地图比例尺及缩放控件
        mBinding.baiduMapViewDriverPickUpPassenger.showZoomControls(false);
        mBinding.baiduMapViewDriverPickUpPassenger.showScaleControl(false);

        // 设置短信按钮监听器
        mBinding.buttonDriverPickUpPassengerText.setOnClickListener(new View.OnClickListener() {
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
                String content=getString(R.string.sms_pick_up_passenger);
                //发送短信
                ContactHelper.sendMessage(getContext(),phone,content);
            }
        });

        // 设置电话按钮监听器
        mBinding.buttonDriverPickUpPassengerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查权限
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionHelper.hasBaseAuth(getContext(), Manifest.permission.CALL_PHONE)==false) {
                        // 未获取则请求权限
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
        mBinding.slideButtonPickUpPassenger.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                // 封装消息
                PickUpPassengerBody body=new PickUpPassengerBody();
                Order order=mOrderViewModel.getOrder().getValue();
                Calendar calendar=Calendar.getInstance();
                Date currentDate=calendar.getTime();

                order.setDepartTime(currentDate);
                body.setOrder(mOrderViewModel.getOrder().getValue());
                BaseMessage message=new BaseMessage(MessageType.PICK_UP_PASSENGER_MESSAGE,body);

                //发送消息
                mMessageHelper.send(message);

                Toast.makeText(getContext(), getString(R.string.hint_confirm_successful), Toast.LENGTH_SHORT).show();
                // 跳转到达目的地界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,new ArriveDestinationFragment(),null);
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
        mBaiduMap = mBinding.baiduMapViewDriverPickUpPassenger.getMap();

        // 获取路径规划对象
        mSearch = RoutePlanSearch.newInstance();

        // 设置路径规划结果监听器
        mSearch.setOnGetRoutePlanResultListener(listener);

        // 路径规划
        initRoutePlan();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBinding.baiduMapViewDriverPickUpPassenger.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBinding.baiduMapViewDriverPickUpPassenger.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBinding.baiduMapViewDriverPickUpPassenger.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
        //TraceHelper.stopGather();
        //TraceHelper.stopTrace();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 获取订单ViewModel
        mOrderViewModel=new ViewModelProvider(getActivity()).get(OrderViewModel.class);
        if(getActivity() instanceof DriverOrderActivity){
            DriverOrderActivity activity=(DriverOrderActivity)getActivity();
            // 设置地图视图
            activity.getTrackOverlay().setBaiduMapView(mBinding.baiduMapViewDriverPickUpPassenger);
        }
    }

    // 权限请求结果处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 获取乘客联系方式
        String phone=mOrderViewModel.getOrder().getValue().getPassengerPhoneNumber();
        String content=getString(R.string.sms_pick_up_passenger);
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

        }
    }

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
                // 设置数据
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                // 语音播报信息
                mTtsUtility.speaking(getString(R.string.hint_pick_up_passenger));
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

    // 路径规划
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