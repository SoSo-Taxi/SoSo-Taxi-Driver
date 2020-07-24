/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/24
 */
package com.sosotaxi.driver.viewModel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.User;
import com.sosotaxi.driver.model.message.ServiceType;
import com.sosotaxi.driver.service.net.LoginTask;
import com.sosotaxi.driver.service.net.QueryDriverTask;
import com.sosotaxi.driver.service.net.UserNetService;
import com.sosotaxi.driver.ui.main.MainActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * 司机ViewModel
 */
public class DriverViewModel extends AndroidViewModel {
    /**
     * 司机对象
     */
    private MutableLiveData<Driver> mDriver;

    /**
     * 司机VO对象
     */
    private MutableLiveData<DriverVo> mDriverVo;

    public DriverViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 获取司机对象
     * @return 司机对象
     */
    public MutableLiveData<Driver> getDriver(){
        if(mDriver==null){
            // 初始化对象
            mDriver=new MutableLiveData<Driver>();
            mDriverVo=new MutableLiveData<DriverVo>();
        }
        return mDriver;
    }

    /**
     * 获取司机VO对象
     * @return 司机VO对象
     */
    public MutableLiveData<DriverVo> getDriverVo(){
        if(mDriver==null){
            // 初始化对象
            mDriver=new MutableLiveData<Driver>();
            mDriverVo=new MutableLiveData<DriverVo>();
        }
        return mDriverVo;
    }

}
