package com.sosotaxi.driver.service.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sosotaxi.driver.model.Driver;

/**
 * @Author 屠天宇
 * @CreateTime 2020/7/25
 * @UpdateTime 2020/7/25
 */

import org.json.JSONException;

import java.io.IOException;

public class DriverStatisticsTask implements Runnable{

    private Driver mDriver;
    private Handler mHandler;

    public DriverStatisticsTask(Driver driver) {
        this.mDriver = driver;
    }

    public DriverStatisticsTask(Driver mDriver, Handler mHandler) {
        this.mDriver = mDriver;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {
        try {
            int servicePoint = DriverStatisticsNetService.postDriverStatistics(mDriver);
            Bundle bundle = new Bundle();
            bundle.putInt("servicePoint",servicePoint);
            Message message = new Message();
            message.setData(bundle);
            mHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
