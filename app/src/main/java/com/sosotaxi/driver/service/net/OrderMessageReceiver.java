package com.sosotaxi.driver.service.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.widget.Toolbar;

import com.sosotaxi.driver.common.Constant;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/19
 * @UpdateTime 2020/7/19
 */
public class OrderMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,intent.getStringExtra(Constant.EXTRA_RESPONSE_MESSAGE), Toast.LENGTH_SHORT).show();
    }
}
