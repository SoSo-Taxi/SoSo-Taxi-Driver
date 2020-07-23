package com.sosotaxi.driver.service.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
public class QueryDriverTask implements Runnable {
    private Driver mDriver;
    private DriverVo mDriverVo;
    private Handler mHandler;

    public QueryDriverTask(Driver driver,DriverVo driverVo,Handler handler){
        mDriver=driver;
        mDriverVo=driverVo;
        mHandler=handler;
    }

    public QueryDriverTask(Driver driver,DriverVo driverVo){
        mDriver=driver;
        mDriverVo=driverVo;
    }

    @Override
    public void run() {
        try {
            // 查询司机信息
            Pair<Boolean,String> result= UserNetService.queryDriver(mDriver,mDriverVo);

            Boolean isSuccessful=result.first;
            String responseMessage=result.second;

            // 填充数据
            Bundle bundle=new Bundle();
            bundle.putBoolean(Constant.EXTRA_IS_SUCCESSFUL,isSuccessful);
            bundle.putString(Constant.EXTRA_RESPONSE_MESSAGE,responseMessage);
            Message message=new Message();
            message.setData(bundle);

            // 发送控制器信息
            //mHandler.sendMessage(message);

        } catch (Exception e) {

            // 填充数据
            Bundle bundle=new Bundle();
            bundle.putString(Constant.EXTRA_ERROR,e.getMessage());
            Message message=new Message();
            message.setData(bundle);

            // 发送控制器信息
            //mHandler.sendMessage(message);
        }
    }
}
