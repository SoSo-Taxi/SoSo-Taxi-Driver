/**
 * @Author 范承祥
 * @CreateTime 2020/7/9
 * @UpdateTime 2020/7/11
 */
package com.sosotaxi.driver.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
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
import android.widget.TextView;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.databinding.FragmentEnterPhoneBinding;
import com.sosotaxi.driver.model.User;
import com.sosotaxi.driver.service.net.IsRegisteredTask;
import com.sosotaxi.driver.viewModel.UserViewModel;

import static android.app.Activity.RESULT_OK;

/**
 * 输入手机号界面
 */
public class EnterPhoneFragment extends Fragment {

    /**
     * 用户ViewModel
     */
    private UserViewModel mUserViewModel;

    private FragmentEnterPhoneBinding mBinding;

    public EnterPhoneFragment() {
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
        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_enter_phone, container, false);
        mBinding.setViewModel(mUserViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 设置继续按钮点击事件
        mBinding.buttonContinue.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  // 获取手机号
                  String areaCodeString=mBinding.textViewAreaCode.getText().toString();
                  String areaCode=areaCodeString.substring(0,areaCodeString.length()-1);
                  String phone=mBinding.editTextEnterPhone.getText().toString();
                  String fullPhone=areaCode+phone;

                  //查询手机号是否已注册
                  new Thread(new IsRegisteredTask(fullPhone,handler)).start();
              }
        });

        // 设置区号点击监听器
        mBinding.textViewAreaCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转区号选择界面
                Intent intent = new Intent(getContext(), SelectAreaCodeActivity.class);
                startActivityForResult(intent, Constant.SELECT_AREA_CODE_REQUEST);
            }
        });

        // 设置手机号输入框字数监听
        mBinding.editTextEnterPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    //未输入时继续按钮不可用
                    mBinding.buttonContinue.setBackgroundColor(getResources().getColor(R.color.colorDisabledButton));
                    mBinding.buttonContinue.setEnabled(false);
                }else{
                    //输入后才可用
                    mBinding.buttonContinue.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    mBinding.buttonContinue.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 填充布局
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode==RESULT_OK) {
            switch (requestCode){
                // 解析返回区号
                case Constant.SELECT_AREA_CODE_REQUEST:
                    int areaCode=data.getIntExtra(Constant.EXTRA_AREA_CODE,86);
                    String areaCodeString="+"+areaCode+" ▼";
                    mBinding.editTextEnterPhone.setText(areaCodeString);
                    break;
            }
        }
    }

    /**
     * UI线程更新处理器
     */
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            mUserViewModel.getUser().getValue().setUserName(bundle.getString(Constant. EXTRA_PHONE));

            // 提示异常信息
            if(bundle.getString(Constant.EXTRA_ERROR)!=null){
                Toast.makeText(getContext(), bundle.getString(Constant.EXTRA_ERROR), Toast.LENGTH_SHORT).show();
                return false;
            }

            boolean isRegistered = bundle.getBoolean(Constant.EXTRA_IS_REGISTERED);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            if (isRegistered) {
                //已注册跳转到输入密码界面
                EnterPasswordFragment enterPasswordFragment = new EnterPasswordFragment();

                // 填充数据
                enterPasswordFragment.setArguments(bundle);

                // 设置转场
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.animator.fragment_slide_left_enter,
                                R.animator.fragment_slide_left_exit,
                                R.animator.fragment_slide_right_enter,
                                R.animator.fragment_slide_right_exit)
                        .add(R.id.frameLayoutLogin, enterPasswordFragment, null)
                        .addToBackStack(null)
                        .commit();

                // 设置返回按钮生效
                LoginActivity loginActivity = (LoginActivity) getActivity();
                loginActivity.setBackUpButtonOn();
            } else {
                // 未注册跳转到输入验证码界面
                EnterVerificationCodeFragment enterVerificationCodeFragment = new EnterVerificationCodeFragment();

                // 填充数据
                enterVerificationCodeFragment.setArguments(bundle);

                // 设置转场
                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.animator.fragment_slide_left_enter,
                                R.animator.fragment_slide_left_exit,
                                R.animator.fragment_slide_right_enter,
                                R.animator.fragment_slide_right_exit)
                        .add(R.id.frameLayoutLogin, enterVerificationCodeFragment, null)
                        .addToBackStack(null)
                        .commit();

                // 设置返回按钮生效
                LoginActivity loginActivity = (LoginActivity) getActivity();
                loginActivity.setBackUpButtonOn();
            }
            return true;
        }
    });
}