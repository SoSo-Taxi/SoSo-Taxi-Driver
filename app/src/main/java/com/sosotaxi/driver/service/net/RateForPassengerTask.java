/**
 * @Author 范承祥
 * @CreateTime 2020/7/25
 * @UpdateTime 2020/7/25
 */
package com.sosotaxi.driver.service.net;

import android.os.Bundle;
import android.os.Message;
import android.util.Pair;

import com.google.gson.Gson;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;

import org.json.JSONObject;
import android.os.Handler;

/**
 * 评价乘客任务
 */
public class RateForPassengerTask implements Runnable {
    /**
     * 订单ID
     */
    private long mOrderId;

    /**
     * 评分
     */
    private double mRate;

    /**
     * 处理器
     */
    private Handler mHandler;

    public RateForPassengerTask(long orderId,double rate,Handler handler){
        mOrderId=orderId;
        mRate=rate;
        mHandler=handler;
    }

    @Override
    public void run() {
        try {
            // 评价乘客
            Pair<Boolean,String> result= OrderNetService.rateForPassenger(mOrderId,mRate);

            Boolean isSuccessful=result.first;
            String responseMessage=result.second;

            // 填充数据
            Bundle bundle=new Bundle();
            bundle.putBoolean(Constant.EXTRA_IS_SUCCESSFUL,isSuccessful);
            bundle.putString(Constant.EXTRA_RESPONSE_MESSAGE,responseMessage);
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
            mHandler.sendMessage(message);
        }
    }
}
