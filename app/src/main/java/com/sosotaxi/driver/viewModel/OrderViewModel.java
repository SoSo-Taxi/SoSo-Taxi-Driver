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

import com.sosotaxi.driver.model.Order;

/**
 * 订单ViewModel
 */
public class OrderViewModel extends AndroidViewModel {
    private MutableLiveData<Order> mOrder;

    public OrderViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Order> getOrder(){
        return mOrder;
    }
}
