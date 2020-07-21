/**
 * @Author 范承祥
 * @CreateTime 2020/7/9
 * @UpdateTime 2020/7/11
 */
package com.sosotaxi.driver.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.User;
import com.sosotaxi.driver.service.net.LoginTask;
import com.sosotaxi.driver.ui.main.MainActivity;
import com.sosotaxi.driver.viewModel.UserViewModel;

public class LoginActivity extends AppCompatActivity {

    /**
     * 用户ViewModel
     */
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        mUserViewModel= new ViewModelProvider(this).get(UserViewModel.class);
        User user=mUserViewModel.getUser().getValue();
        String username=user.getUserName();
        String password=user.getPassword();

        if(username!=""&&password!=""){
            // 存在用户信息则自动登陆
            new Thread(new LoginTask(user,handler)).start();
        }else{
            // 不存在用户信息则设置登陆界面布局
            setContentView(R.layout.activity_login);
            Toolbar toolbar = findViewById(R.id.toolbarLogin);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            // 跳转登陆界面
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frameLayoutLogin,new EnterPhoneFragment(),null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 填充菜单
        getMenuInflater().inflate(R.menu.activity_login, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // 返回上一级页面
                FragmentManager fragmentManager=getSupportFragmentManager();
                getSupportFragmentManager().popBackStack();
                if(fragmentManager.getBackStackEntryCount()==1){
                    setBackUpButtonOff();
                }
                break;
            case R.id.menu_login_skip:
                // 直接进入主页面
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置返回箭头可用
     */
    public void setBackUpButtonOn(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 设置返回箭头不可用
     */
    public void setBackUpButtonOff(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    /**
     * UI线程更新处理器
     */
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            // 提示异常信息
            if(bundle.getString(Constant.EXTRA_ERROR)!=null){
                Toast.makeText(getApplicationContext(), bundle.getString(Constant.EXTRA_ERROR), Toast.LENGTH_SHORT).show();
                return false;
            }

            boolean isAuthorized = bundle.getBoolean(Constant.EXTRA_IS_AUTHORIZED);

            if(isAuthorized){
                // 跳转主界面
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                // 验证失败提示密码错误
                Toast.makeText(getApplicationContext(), R.string.error_password_incorrect, Toast.LENGTH_SHORT).show();

                // 跳转登陆界面
                FragmentManager fragmentManager=getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.frameLayoutLogin,new EnterPhoneFragment(),null);
                fragmentTransaction.commit();
            }
            return true;
        }
    });

}