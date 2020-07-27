/**
 * @Author 范承祥
 * @CreateTime 2020/7/19
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.ui.overlay;

import android.os.Handler;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.sosotaxi.driver.common.Constant;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 轨迹覆盖类
 */
public class TrackOverlay {
    /**
     * 间隔时间
     */
    private static final int TIME_INTERVAL = Constant.GATHER_INTERVAL*100;

    /**
     * 图标移动距离
     */
    private static final double DISTANCE = 0.00002;

    /**
     * 路径
     */
    private ConcurrentLinkedQueue<LatLng> mLatestPointQueue;

    /**
     * 标记
     */
    private Marker mMoveMarker;

    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;

    /**
     * 控制器对象
     */
    private Handler mHandler;

    private MapView mBaiduMapView;

    public TrackOverlay(Handler handler) {
        mLatestPointQueue = new ConcurrentLinkedQueue<LatLng>();
        this.mHandler = handler;
    }

    /**
     * 设置地图视图
     * @param baiduMapView 百度视图
     */
    public void setBaiduMapView(MapView baiduMapView) {
        this.mBaiduMapView = baiduMapView;
        this.mBaiduMap = baiduMapView.getMap();

        // 设置标志
        OverlayOptions markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromAsset("Icon_current_marker.png"))
                .position(new LatLng(39.892204,116.54157))
                .rotate((float)0)
                .zIndex(20);
        mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
    }

    /**
     * 获取地图视图
     * @return
     */
    public MapView getBaiduMapView() {
        return mBaiduMapView;
    }

    /**
     * 添加定位点
     * @param point 定位点
     */
    public void addLatestPoint(LatLng point) {
        mLatestPointQueue.add(point);
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }

    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 循环进行移动逻辑
     */
    public void moveLooper() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (mBaiduMapView == null || mLatestPointQueue.isEmpty()) {
                        continue;
                    }
                    if (mLatestPointQueue.size() == 1) {
                        final LatLng startPoint = mLatestPointQueue.peek();
                        mMoveMarker.setPosition(startPoint);
                    } else {
                        final LatLng startPoint = mLatestPointQueue.poll();
                        final LatLng endPoint = mLatestPointQueue.peek();
                        mMoveMarker.setPosition(startPoint);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // 刷新标记角度
                                if (mBaiduMapView == null) {
                                    return;
                                }
                                mMoveMarker.setRotate((float) getAngle(startPoint,
                                        endPoint));
                            }
                        });
                        double slope = getSlope(startPoint, endPoint);
                        // 是不是正向的标示
                        boolean isReverse = (startPoint.latitude > endPoint.latitude);

                        double intercept = getInterception(slope, startPoint);

                        double xMoveDistance = isReverse ? getXMoveDistance(slope) : -1 * getXMoveDistance(slope);


                        for (double j = startPoint.latitude; !((j > endPoint.latitude) ^ isReverse);
                             j = j - xMoveDistance) {
                            LatLng latLng = null;
                            if (slope == Double.MAX_VALUE) {
                                latLng = new LatLng(j, startPoint.longitude);
                            } else {
                                latLng = new LatLng(j, (j - intercept) / slope);
                            }

                            final LatLng finalLatLng = latLng;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBaiduMapView == null) {
                                        return;
                                    }
                                    mMoveMarker.setPosition(finalLatLng);
                                }
                            });
                            try {
                                Thread.sleep(TIME_INTERVAL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }.start();
    }
}
