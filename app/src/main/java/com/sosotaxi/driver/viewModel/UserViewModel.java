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
import com.sosotaxi.driver.model.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * 用户ViewModel
 */
public class UserViewModel extends AndroidViewModel {
    private SharedPreferences mSharedPreferences;
    private MutableLiveData<User> mUser;

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<User> getUser(){
        if(mUser==null){
            mUser= new MutableLiveData<User>();
            loadUser();
        }
        return mUser;
    }

    private void loadUser(){
        // 获取已登录用户信息
        mSharedPreferences =getApplication().getSharedPreferences(Constant.SHARE_PREFERENCE_LOGIN, MODE_PRIVATE);
        String username= mSharedPreferences.getString(Constant.USERNAME,"");
        String password= mSharedPreferences.getString(Constant.PASSWORD,"");
        String token= mSharedPreferences.getString(Constant.TOKEN,"");
        User user=new User();
        user.setUserName(username);
        user.setPassword(password);
        mUser.setValue(user);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 保存用户信息
        SharedPreferences sharedPreferences=getApplication().getSharedPreferences(Constant.SHARE_PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constant.USERNAME,mUser.getValue().getUserName());
        editor.putString(Constant.PASSWORD,mUser.getValue().getPassword());
        editor.putString(Constant.TOKEN,mUser.getValue().getToken());
        editor.commit();
    }
}
