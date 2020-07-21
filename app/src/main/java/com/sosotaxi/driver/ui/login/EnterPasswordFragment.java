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
import com.sosotaxi.driver.databinding.FragmentEnterPasswordBinding;
import com.sosotaxi.driver.model.User;
import com.sosotaxi.driver.service.net.LoginTask;
import com.sosotaxi.driver.ui.main.MainActivity;
import com.sosotaxi.driver.viewModel.UserViewModel;

/**
 * 输入密码界面
 */
public class EnterPasswordFragment extends Fragment {

    /**
     * 用户ViewModel
     */
    private UserViewModel mUserViewModel;

    private FragmentEnterPasswordBinding mBinding;

    public EnterPasswordFragment() {
        // 所需空构造器
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
        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_enter_password, container, false);
        mBinding.setViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 设置密码输入框监听
        mBinding.editTextEnterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<8){
                    // 密码小于8位时确认按钮不可用
                    mBinding.buttonEnterPasswordConfirm.setBackgroundColor(getResources().getColor(R.color.colorDisabledButton));
                    mBinding.buttonEnterPasswordConfirm.setEnabled(false);
                }else{
                    // 密码大于等于8位时确认按钮才可用
                    mBinding.buttonEnterPasswordConfirm.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    mBinding.buttonEnterPasswordConfirm.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //设置确认按钮点击事件
        mBinding.buttonEnterPasswordConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用户登陆
                new Thread(new LoginTask(mUserViewModel.getUser().getValue(),handler)).start();
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

            boolean isAuthorized = bundle.getBoolean(Constant.EXTRA_IS_AUTHORIZED);

            if(isAuthorized){
                // 跳转主界面
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                // 验证失败清空密码并提示密码错误
                mBinding.editTextEnterPassword.setText("");
                mBinding.editTextEnterPassword.setError(getString(R.string.error_password_incorrect));
            }
            return true;
        }
    });
}