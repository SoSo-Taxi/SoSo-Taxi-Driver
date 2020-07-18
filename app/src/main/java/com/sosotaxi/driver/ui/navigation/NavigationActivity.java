/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.ui.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRouteGuideManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.utils.PermissionHelper;

import java.io.File;

import static com.baidu.mapapi.BMapManager.getContext;

public class NavigationActivity extends AppCompatActivity {

    private FrameLayout mFrameLayoutNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // 获取控件
        mFrameLayoutNavigation = findViewById(R.id.frameLayoutNavigation);

        Bundle bundle = new Bundle();

        // true为真实导航，false为模拟导航
        bundle.putBoolean(BNaviCommonParams.ProGuideKey.IS_REALNAVI, false);
        View navigationView = BaiduNaviManagerFactory.getRouteGuideManager().onCreate(this,
                new IBNRouteGuideManager.OnNavigationListener() {
                    @Override
                    public void onNaviGuideEnd() {
                        Toast.makeText(getContext(), R.string.hint_navigation_finish, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {

                    }
                }, bundle);

        mFrameLayoutNavigation.addView(navigationView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        BaiduNaviManagerFactory.getRouteGuideManager().onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        super.onStart();
        BaiduNaviManagerFactory.getRouteGuideManager().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        BaiduNaviManagerFactory.getRouteGuideManager().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getRouteGuideManager().onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        BaiduNaviManagerFactory.getRouteGuideManager().onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaiduNaviManagerFactory.getRouteGuideManager().onDestroy(false);
    }
}