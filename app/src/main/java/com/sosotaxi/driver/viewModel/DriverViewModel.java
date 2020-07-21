/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/21
 */
package com.sosotaxi.driver.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sosotaxi.driver.model.Driver;

/**
 * 司机ViewModel
 */
public class DriverViewModel extends AndroidViewModel {
    private MutableLiveData<Driver> mDriver;

    public DriverViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Driver> getDriver(){
        return mDriver;
    }
}
