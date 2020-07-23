/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/21
 */
package com.sosotaxi.driver.viewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.message.ServiceType;
import com.sosotaxi.driver.service.net.QueryDriverTask;

/**
 * 司机ViewModel
 */
public class DriverViewModel extends AndroidViewModel {
    private MutableLiveData<Driver> mDriver;
    private MutableLiveData<DriverVo> mDriverVo;

    public DriverViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Driver> getDriver(){
        if(mDriver==null){
            mDriver=new MutableLiveData<Driver>();
        }
        if(mDriverVo==null){
            mDriverVo=new MutableLiveData<DriverVo>();
        }
        loadDriver();
        return mDriver;
    }

    public MutableLiveData<DriverVo> getDriverVo(){
        if(mDriver==null){
            mDriver=new MutableLiveData<Driver>();
        }
        if(mDriverVo==null){
            mDriverVo=new MutableLiveData<DriverVo>();
        }
        loadDriver();
        return mDriverVo;
    }

    private void loadDriver(){
        Driver driver=new Driver();
        DriverVo driverVo=new DriverVo();
//        new Thread(new QueryDriverTask(driver,driverVo)).start();
        //mDriver.setValue(driver);
        //mDriverVo.setValue(driverVo);
        driver.setAvailable(false);
        driver.setServiceType((short) ServiceType.ECONOMIC.ordinal());
        driverVo.setServiceType((short) ServiceType.ECONOMIC.ordinal());
        driver.setCurrentPoint(new LocationPoint(40.05087,116.30142));
        mDriver.setValue(driver);
        mDriverVo.setValue(driverVo);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
//        // 保存用户信息
//        SharedPreferences sharedPreferences=getApplication().getSharedPreferences(Constant.SHARE_PREFERENCE_LOGIN, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=sharedPreferences.edit();
//        editor.putString(Constant.USERNAME,mDriver.getValue().get());
//        editor.putString(Constant.PASSWORD,mDriver.getValue().getPassword());
//        editor.putString(Constant.TOKEN,mDriver.getValue().getToken());
//        editor.commit();
    }
}
