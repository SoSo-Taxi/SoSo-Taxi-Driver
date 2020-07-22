/**
 * @Author 范承祥
 * @CreateTime 2020/7/9
 * @UpdateTime 2020/7/11
 */
package com.sosotaxi.driver.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.databinding.FragmentCreatePasswordBinding;
import com.sosotaxi.driver.model.User;
import com.sosotaxi.driver.service.net.LoginTask;
import com.sosotaxi.driver.service.net.RegisterTask;
import com.sosotaxi.driver.ui.main.MainActivity;
import com.sosotaxi.driver.viewModel.UserViewModel;

/**
 * 创建密码界面
 */
public class CreatePasswordFragment extends Fragment {

    /**
     * 用户ViewModel
     */
    private UserViewModel mUserViewModel;

    private FragmentCreatePasswordBinding mBinding;

    /**
     * 密码长度标志位
     */
    private boolean mFlag1;

    /**
     * 确认密码长度标志位
     */
    private boolean mFlag2;

    public CreatePasswordFragment() {
        // 初始化密码长度标志位
        mFlag1=false;
        mFlag2=false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取用户ViewModel
        mUserViewModel=new ViewModelProvider(getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_create_password, container, false);
        mBinding.setViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 设置输入改变监听器
        mBinding.editTextCreatePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 检查密码位数
                mFlag1=checkPassword(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 设置输入改变监听器
        mBinding.editTextCreatePasswordConfirmed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 检查密码位数
                mFlag2=checkPassword(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 设置确认按钮点击事件
        mBinding.buttonCreatePasswordConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=mBinding.editTextCreatePassword.getText().toString();
                String passwordConfirmed=mBinding.editTextCreatePasswordConfirmed.getText().toString();

                if(!password.equals(passwordConfirmed)){
                    // 提示密码不一致
                    mBinding.editTextCreatePasswordConfirmed.setError(getString(R.string.error_password_different));

                }else{
                    // 验证密码是否符合要求
                    boolean result=password.matches(getString(R.string.regex_password));

                    if(result==true){
                        User user=mUserViewModel.getUser().getValue();
                        user.setRole("driver");
                        // 注册用户
                        new Thread(new RegisterTask(user,handler)).start();
                    }else{
                        // 清空密码输入框并提示密码不符合要求
                        mBinding.editTextCreatePassword.setText("");
                        mBinding.editTextCreatePasswordConfirmed.setText("");
                        mBinding.editTextCreatePassword.setError(getString(R.string.error_password_not_match));
                    }
                }
            }
        });
        // 填充布局
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 检查密码位数
     * @param s 密码
     * @return  flag 符合标志
     */
    private boolean checkPassword(CharSequence s){
        if(mFlag1&&mFlag2){
            // 两个密码输入框同时符合要求确认按钮可点击
            mBinding.buttonCreatePasswordConfirm.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            mBinding.buttonCreatePasswordConfirm.setEnabled(true);
        }else{
            // 至少一个密码输入框不符合要求确认按钮不可点击
            mBinding.buttonCreatePasswordConfirm.setBackgroundColor(getResources().getColor(R.color.colorDisabledButton));
            mBinding.buttonCreatePasswordConfirm.setEnabled(false);
        }

        if(s.length()<8){
            // 密码位数小于8位不符合要求
            return false;
        }else{
            // 密码位数大于等于8位符合要求
            return true;
        }
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
                Toast.makeText(getContext(), bundle.getString(Constant.EXTRA_ERROR), Toast.LENGTH_SHORT).show();
                return false;
            }

            boolean isSuccessful = bundle.getBoolean(Constant.EXTRA_IS_SUCCESSFUL);
            boolean isAuthorized=bundle.getBoolean(Constant.EXTRA_IS_AUTHORIZED);

            if(isSuccessful){
                // 获取用户对象
                User user=mUserViewModel.getUser().getValue();
                user.setRememberMe(true);

                // 登陆
                new Thread(new LoginTask(user,handler)).start();

                // 跳转主界面
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else if(isAuthorized==false){
                // 提示注册失败
                Toast.makeText(getContext(), R.string.error_register_failed,Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });
}