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

import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * 用户ViewModel
 */
public class UserViewModel extends AndroidViewModel {
    /**
     * 共享偏好
     */
    private SharedPreferences mSharedPreferences;

    /**
     * 用户对象
     */
    private MutableLiveData<User> mUser;

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 获取用户对象
     * @return 用户对象
     */
    public MutableLiveData<User> getUser(){
        if(mUser==null){
            // 初始化对象
            mUser= new MutableLiveData<User>();
            loadUser();
        }
        return mUser;
    }

    /**
     * 加载用户
     */
    private void loadUser(){
        // 获取已登录用户信息
        mSharedPreferences =getApplication().getSharedPreferences(Constant.SHARE_PREFERENCE_LOGIN, MODE_PRIVATE);
        String username= mSharedPreferences.getString(Constant.USERNAME,"");
        String password= mSharedPreferences.getString(Constant.PASSWORD,"");
        String token= mSharedPreferences.getString(Constant.TOKEN,"");
        User user=new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setToken(token);
        mUser.setValue(user);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 保存用户信息
        SharedPreferences sharedPreferences=getApplication().getSharedPreferences(Constant.SHARE_PREFERENCE_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(mUser.getValue().getUserName()!=null&&mUser.getValue().getUserName().isEmpty()==false){
            editor.putString(Constant.USERNAME,mUser.getValue().getUserName());
        }
        if(mUser.getValue().getPassword()!=null&&mUser.getValue().getPassword().isEmpty()==false){
            editor.putString(Constant.PASSWORD,mUser.getValue().getPassword());
        }
        if(mUser.getValue().getToken()!=null&&mUser.getValue().getToken().isEmpty()==false){
            editor.putString(Constant.TOKEN,mUser.getValue().getToken());
        }
        editor.apply();
    }
}
