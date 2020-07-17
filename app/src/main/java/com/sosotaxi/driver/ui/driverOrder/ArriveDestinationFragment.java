/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
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
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;
import com.sosotaxi.driver.utils.ContactHelper;
import com.sosotaxi.driver.utils.NavigationHelper;
import com.sosotaxi.driver.utils.PermissionHelper;

import java.util.List;

/**
 *
 */
public class ArriveDestinationFragment extends Fragment {

    private BNRoutePlanNode mStartNode;

    private BNRoutePlanNode mEndNode;

    private BaiduMap mBaiduMap;

    private RoutePlanSearch mSearch;

    private MapView mBaiduMapView;
    private ConstraintLayout mConstraintLayoutNavigation;
    private TextView mTextViewNavigation;
    private ImageButton mImageButtonNavigation;
    private SlideButton mSlideButton;

    public ArriveDestinationFragment() {
        // 所需空构造器
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 填充布局
        return inflater.inflate(R.layout.fragment_arrive_destination, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBaiduMapView=getActivity().findViewById(R.id.baiduMapViewDriverArriveDestination);
        mConstraintLayoutNavigation = getActivity().findViewById(R.id.constraintLayoutArriveDestinationNavigation);
        mTextViewNavigation=getActivity().findViewById(R.id.textViewDriverOrderArriveDestinationNavigation);
        mImageButtonNavigation = getActivity().findViewById(R.id.imageButtonDriverArriveDestinationNavigation);
        mSlideButton=getActivity().findViewById(R.id.slideButtonArriveDestination);

        // 不显示地图比例尺及缩放控件
        mBaiduMapView.showZoomControls(false);
        mBaiduMapView.showScaleControl(false);

        // 获取百度地图对象
        mBaiduMap = mBaiduMapView.getMap();

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PermissionHelper.hasBaseAuth(getContext(), Constant.AUTH_ARRAY_NAVIGATION) == false) {
                        requestPermissions(Constant.AUTH_ARRAY_NAVIGATION, Constant.PERMISSION_SEND_SMS_REQUEST);
                        return;
                    }

                }

                if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
//                    if (mCurrentLatitude == 0 && mCurrentLongitude == 0) {
//                        return;
//                    }
                    mStartNode = new BNRoutePlanNode.Builder()
                            .latitude(39.98340)
                            .longitude(116.42532)
                            .name("奥体中心")
                            .description("奥体中心")
                            .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                            .build();
                    mEndNode = new BNRoutePlanNode.Builder()
                            .latitude(39.90882)
                            .longitude(116.39750)
                            .name("北京天安门")
                            .description("北京天安门")
                            .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                            .build();

                    NavigationHelper.routePlanToNavigation(getContext(),mStartNode, mEndNode, null);
                }
            }
        };

        mConstraintLayoutNavigation.setOnClickListener(onClickListener);
        mImageButtonNavigation.setOnClickListener(onClickListener);
        mTextViewNavigation.setOnClickListener(onClickListener);

        mSlideButton.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                Toast.makeText(getContext(), "确认成功!", Toast.LENGTH_SHORT).show();
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

        // 获取路径规划对象
        mSearch = RoutePlanSearch.newInstance();

        // 设置路径规划结果监听器
        mSearch.setOnGetRoutePlanResultListener(listener);

        NavigationHelper.init();
        initRoutePlan();
    }

    private void initRoutePlan() {
        Toast.makeText(getContext(), R.string.hint_route_planning, Toast.LENGTH_SHORT).show();
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "奥体中心");
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "天安门广场");
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }

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
            //创建DrivingRouteOverlay实例
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
            // 清除原有路线
            overlay.removeFromMap();
            List<DrivingRouteLine> routes = drivingRouteResult.getRouteLines();
            if (routes != null && routes.size() > 0) {
                //获取路径规划数据
                //为DrivingRouteOverlay实例设置数据
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                //在地图上绘制路线
                overlay.addToMap(false);
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
}