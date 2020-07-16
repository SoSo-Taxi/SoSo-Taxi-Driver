/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.ui.navigation.NavigationActivity;
import com.sosotaxi.driver.ui.overlay.DrivingRouteOverlay;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 到达上车地点界面
 */
public class ArriveStartingPointFragment extends Fragment {

    private static final String[] sAuthBaseArray = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private String mSdCardPath;

    private BNRoutePlanNode mStartNode;

    private BNRoutePlanNode mEndNode;

    private String mStartPlace;

    private String mCurrentPlace;

    private String mEndPlace;

    private String mStartCity;

    private String mCurrentCity;

    private String mEndCity;

    private double mCurrentLatitude;

    private double mCurrentLongitude;

    private LocationManager mLocationManager;

    private LocationListener mLocationListener;

    private RoutePlanSearch mSearch;

    private BaiduMap mBaiduMap;

    private MapView mBaiduMapView;
    private ConstraintLayout mConstraintLayoutNavigation;
    private TextView mTextViewNavigation;
    private ImageButton mImageButtonNavigation;
    private TextView mTextViewFrom;
    private TextView mTextViewTo;
    private SlideButton mSlideButton;


    public ArriveStartingPointFragment() {
        // 所需空构造器
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mCurrentLatitude = location.getLatitude();
                mCurrentLongitude = location.getLongitude();

                // DEBUG
                Toast.makeText(getContext(), mCurrentLatitude + ", " + mCurrentLongitude, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 填充布局
        return inflater.inflate(R.layout.fragment_arrive_starting_point, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRoutePlanNode();
        if (initDirs()) {
            initNavi();
        }
        initLocation();

        mBaiduMapView = getActivity().findViewById(R.id.baiduMapViewDriverOrderArriveStartingPoint);
        mImageButtonNavigation = getActivity().findViewById(R.id.imageButtonDriverOrderArriveStartingPointNavigation);
        mConstraintLayoutNavigation = getActivity().findViewById(R.id.constraintLayoutArriveStartingPointNavigation);
        mTextViewNavigation=getActivity().findViewById(R.id.textViewDriverOrderArriveStartingPointNavigation);
        mSlideButton = getActivity().findViewById(R.id.slideButtonArriveStartingPoint);
        mTextViewFrom = getActivity().findViewById(R.id.textViewDriverOrderArriveStartingPointDetailFrom);
        mTextViewTo = getActivity().findViewById(R.id.textViewDriverOrderArriveStartingPointDetailTo);

        mBaiduMapView.showZoomControls(false);
        mBaiduMapView.showScaleControl(false);

        mBaiduMap = mBaiduMapView.getMap();

        mSearch = RoutePlanSearch.newInstance();

        mSearch.setOnGetRoutePlanResultListener(listener);

        mStartCity = "北京";
        mCurrentCity = "北京";
        mEndCity = "北京";

        mStartPlace = mTextViewFrom.getText().toString();
        mCurrentPlace = "西二旗地铁站";
        mEndPlace = mTextViewTo.getText().toString();

        mSlideButton.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                Toast.makeText(getContext(), "确认成功!", Toast.LENGTH_SHORT).show();
                // 跳转接到乘客界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                Fragment currentFragment=getActivity().getSupportFragmentManager().getFragments().get(0);
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.hide(currentFragment);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,new PickUpPassengerFragment(),null);
                fragmentTransaction.commit();
            }
        });

        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
//                    if (mCurrentLatitude == 0 && mCurrentLongitude == 0) {
//                        return;
//                    }
                    BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                            .latitude(40.05087)
                            .longitude(116.30142)
                            .name("百度大厦")
                            .description("百度大厦")
                            .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                            .build();
                    BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                            .latitude(39.90882)
                            .longitude(116.39750)
                            .name("北京天安门")
                            .description("北京天安门")
                            .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                            .build();

                    routePlanToNavi(sNode, eNode, null);
                }
            }
        };

        mConstraintLayoutNavigation.setOnClickListener(onClickListener);
        mImageButtonNavigation.setOnClickListener(onClickListener);
        mTextViewNavigation.setOnClickListener(onClickListener);

    }

    @Override
    public void onStart() {
        super.onStart();
        initRoutePlan();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mBaiduMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mBaiduMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mBaiduMapView.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }
    }

    private void initRoutePlan() {
        Toast.makeText(getContext(), "正在规划路径", Toast.LENGTH_SHORT).show();
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(mCurrentCity, mCurrentPlace);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName(mStartCity, mStartPlace);
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }

    private void initRoutePlanNode() {
        mStartNode = new BNRoutePlanNode.Builder()
                .latitude(40.050969)
                .longitude(116.300821)
                .name("Baidu Building")
                .description("Baidu Building")
                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                .build();
        mEndNode = new BNRoutePlanNode.Builder()
                .latitude(39.908749)
                .longitude(116.397491)
                .name("Tian'an men Square")
                .description("Tian'an men Square")
                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                .build();
    }

    private boolean initDirs() {
        mSdCardPath = getSdcardDir();
        if (mSdCardPath == null) {
            return false;
        }
        File file = new File(mSdCardPath, Constant.APP_FOLDER_NAME);
        if (file.exists() == false) {
            try {
                file.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean hasBasePhoneAuth() {
        PackageManager packageManager = getActivity().getPackageManager();
        for (String auth : sAuthBaseArray) {
            if (packageManager.checkPermission(auth, getActivity().getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                getActivity().requestPermissions(sAuthBaseArray, Constant.AUTH_BASE_REQUEST);
                return;
            }
        }
        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            return;
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(getContext(),
                mSdCardPath, Constant.APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String authinfo;
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        Toast.makeText(getContext(), authinfo, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void initStart() {
                        Toast.makeText(getContext(), "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(getContext(), "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();

                        // 初始化tts
                        initTTS();
                    }

                    @Override
                    public void initFailed(int errCode) {
                        Toast.makeText(getContext(), "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }

                });

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initTTS() {
        BaiduNaviManagerFactory.getTTSManager().initTTS(getContext(), getSdcardDir(), Constant.APP_FOLDER_NAME, "21383548");
    }

    private void initLocation() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "没有权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000, mLocationListener);
        }
    }

    private void routePlanToNavi(BNRoutePlanNode sNode, BNRoutePlanNode eNode, final Bundle bundle) {
        sNode = new BNRoutePlanNode.Builder()
                .latitude(40.05087)
                .longitude(116.30142)
                .name("百度大厦")
                .description("百度大厦")
                .coordinateType(BNRoutePlanNode.CoordinateType.GCJ02)
                .build();
        eNode = new BNRoutePlanNode.Builder()
                .latitude(39.90882)
                .longitude(116.39750)
                .name("北京天安门")
                .description("北京天安门")
                .coordinateType(BNRoutePlanNode.CoordinateType.GCJ02)
                .build();
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(getContext(),
                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(getContext(),
                                        "算路成功", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(getContext(),
                                        "算路失败", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(getContext(),
                                        "算路成功准备进入导航", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getContext(), NavigationActivity.class);
                                getActivity().startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
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
            List<DrivingRouteLine> routes = drivingRouteResult.getRouteLines();
            if (routes != null && routes.size() > 0) {
                //获取路径规划数据,(以返回的第一条路线为例）
                //为DrivingRouteOverlay实例设置数据
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                //在地图上绘制DrivingRouteOverlay
                overlay.addToMap(true);
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