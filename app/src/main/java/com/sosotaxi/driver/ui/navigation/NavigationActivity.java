package com.sosotaxi.driver.ui.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRouteGuideManager;
import com.sosotaxi.driver.R;

public class NavigationActivity extends AppCompatActivity {

    private FrameLayout mFrameLayoutNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mFrameLayoutNavigation=findViewById(R.id.frameLayoutNavigation);

        Bundle bundle = new Bundle();

        // true为真实导航，false为模拟导航
        bundle.putBoolean(BNaviCommonParams.ProGuideKey.IS_REALNAVI, false);
        View navigationView= BaiduNaviManagerFactory.getRouteGuideManager().onCreate(this,
                new IBNRouteGuideManager.OnNavigationListener() {
                    @Override
                    public void onNaviGuideEnd() {
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