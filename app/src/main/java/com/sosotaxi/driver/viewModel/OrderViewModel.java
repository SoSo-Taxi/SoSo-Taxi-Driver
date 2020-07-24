/**
 * @Author 范承祥
 * @CreateTime 2020/7/21
 * @UpdateTime 2020/7/24
 */
package com.sosotaxi.driver.viewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.google.gson.Gson;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;
import com.sosotaxi.driver.model.LocationPoint;
import com.sosotaxi.driver.model.Order;

import static android.content.Context.MODE_PRIVATE;

/**
 * 订单ViewModel
 */
public class OrderViewModel extends AndroidViewModel {
    /**
     * 订单对象
     */
    private MutableLiveData<Order> mOrder;

    public OrderViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 获取订单对象
     * @return 订单对象
     */
    public MutableLiveData<Order> getOrder(){
        if(mOrder==null){
            // 初始化对象
            mOrder=new MutableLiveData<Order>();
        }
        return mOrder;
    }
}
