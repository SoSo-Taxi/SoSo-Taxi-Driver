/**
 * @Author 范承祥
 * @CreateTime 2020/7/17
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.ui.navigation.NavigationActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.baidu.mapapi.BMapManager.getContext;

/**
 * 导航帮手类
 */
public class NavigationHelper {

    /**
     * SD卡路径
     */
    private static String sSdCardPath;

    /**
     * 初始化导航
     */
    public static void init(){
        // 判断SD卡路径是否初始化
        if(initDirectory()){
            // 已初始化则导航初始化
            initNavigation();
        }
    }

    /**
     * 规划路线并开始导航
     * @param context 上下文
     * @param sNode 起始节点
     * @param eNode 中至节点
     * @param bundle 数据束
     */
    public static void routePlanToNavigation(final Context context, BNRoutePlanNode sNode, BNRoutePlanNode eNode, final Bundle bundle) {
        if(sNode==null||eNode==null){
            return;
        }
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
                                Toast.makeText(context,
                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(context,
                                        "算路成功", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(context,
                                        "算路失败", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(context,
                                        "算路成功准备进入导航", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(context, NavigationActivity.class);
                                context.startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    /**
     * 初始化导航
     */
    private static void initNavigation() {
        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            return;
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(getContext(),
                sSdCardPath, Constant.APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

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

    /**
     * 初始化导航语音
     */
    private static void initTTS() {
        BaiduNaviManagerFactory.getTTSManager().initTTS(getContext(), getSdcardDirectory(), Constant.APP_FOLDER_NAME, Constant.TTS_APP_ID);
    }

    /**
     * 初始化SD卡路径
     * @return 是否已初始化
     */
    private static boolean initDirectory() {
        sSdCardPath = getSdcardDirectory();
        if (sSdCardPath == null) {
            return false;
        }
        File file = new File(sSdCardPath, Constant.APP_FOLDER_NAME);
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

    /**
     * 获取SD卡路径
     * @return
     */
    private static String getSdcardDirectory() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
}
