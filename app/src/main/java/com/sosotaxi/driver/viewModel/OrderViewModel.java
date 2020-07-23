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

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.LocationPoint;
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
        if(mOrder==null){
            mOrder=new MutableLiveData<Order>();
        }
        loadOrder();
        return mOrder;
    }

    private void loadOrder(){
        Order order=new Order();
        order.setPassengerPhoneNumber("8613889889889");
        order.setCity("北京");
        order.setDepartName("奥体中心");
        order.setDepartPoint(new LocationPoint(39.98340,116.42532));
        order.setDestinationName("北京天安门");
        order.setDestinationPoint(new LocationPoint(39.90882,116.39750));

        mOrder.setValue(order);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
//        // 保存用户信息
//        SharedPreferences sharedPreferences=getApplication().getSharedPreferences(Constant.SHARE_PREFERENCE_LOGIN, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=sharedPreferences.edit();
//        editor.putString(Constant.USERNAME,mUser.getValue().getUserName());
//        editor.putString(Constant.PASSWORD,mUser.getValue().getPassword());
//        editor.putString(Constant.TOKEN,mUser.getValue().getToken());
//        editor.commit();
    }
}
