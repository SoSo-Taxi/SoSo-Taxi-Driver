/**
 * @Author 范承祥
 * @CreateTime 2020/7/13
 * @UpdateTime 2020/7/13
 */
package com.sosotaxi.driver.service.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.User;

/**
 * 登陆任务
 */
public class LoginTask implements Runnable {

    /**
     * 用户
     */
    private User mUser;

    /**
     * 处理器
     */
    private Handler mHandler;

    public LoginTask(User user, Handler handler){
        mUser=user;
        mUser.setRememberMe(true);
        mUser.setUserId(null);
        mHandler=handler;
    }

    @Override
    public void run() {
        try {
            String response="";

            boolean isAuthorized= LoginNetService.login(mUser);

            // 填充数据
            Bundle bundle=new Bundle();
            bundle.putString(Constant.EXTRA_PHONE,mUser.getUserName());
            bundle.putString(Constant.EXTRA_PASSWORD,mUser.getPassword());
            bundle.putBoolean(Constant.EXTRA_IS_AUTHORIZED,isAuthorized);
            bundle.putString(Constant.EXTRA_TOKEN,mUser.getToken());
            bundle.putString(Constant.EXTRA_RESPONSE_MESSAGE,response);
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
