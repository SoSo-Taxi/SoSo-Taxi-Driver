package com.sosotaxi.driver.service.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.google.gson.Gson;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;

import org.json.JSONObject;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/23
 * @UpdateTime 2020/7/23
 */
public class QueryDriverTask implements Runnable {
    private Driver mDriver;
    private DriverVo mDriverVo;
    private Handler mHandler;

    public QueryDriverTask(Driver driver,Handler handler){
        mDriver=driver;
        mHandler=handler;
    }

    @Override
    public void run() {
        try {
            // 查询司机信息
            Pair<Boolean,String> result= UserNetService.queryDriver(mDriver);

            Boolean isSuccessful=result.first;
            String responseMessage=result.second;
            JSONObject jsonObject=new JSONObject(responseMessage);
            String json=jsonObject.getString("data");
            Gson gson=new Gson();
            mDriver=gson.fromJson(json,Driver.class);
            mDriverVo=gson.fromJson(json,DriverVo.class);

            // 填充数据
            Bundle bundle=new Bundle();
            bundle.putBoolean(Constant.EXTRA_IS_SUCCESSFUL,isSuccessful);
            bundle.putString(Constant.EXTRA_RESPONSE_MESSAGE,responseMessage);
            bundle.putParcelable(Constant.EXTRA_DRIVER,mDriver);
            bundle.putParcelable(Constant.EXTRA_DRIVER_VO,mDriverVo);
            Message message=new Message();
            message.setData(bundle);

            // 发送控制器信息
            mHandler.sendMessage(message);

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
