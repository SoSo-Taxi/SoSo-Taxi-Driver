/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.OnToolbarListener;
import com.sosotaxi.driver.common.TTSUtility;
import com.sosotaxi.driver.databinding.FragmentArriveStartingPointBinding;
import com.sosotaxi.driver.databinding.FragmentRankPassengerBinding;
import com.sosotaxi.driver.service.net.RateForPassengerTask;
import com.sosotaxi.driver.ui.main.MainActivity;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;
import com.sosotaxi.driver.viewModel.DriverViewModel;
import com.sosotaxi.driver.viewModel.OrderViewModel;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 评价乘客界面
 */
public class RankPassengerFragment extends Fragment {

    /**
     * 订单ViewModel
     */
    private OrderViewModel mOrderViewModel;

    /**
     * 数据绑定对象
     */
    private FragmentRankPassengerBinding mBinding;

    /**
     * 语音播报对象
     */
    private TTSUtility mTtsUtility;

    public RankPassengerFragment() {
        // 获取语音播报对象
        mTtsUtility=TTSUtility.getInstance(getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取订单ViewModel
        mOrderViewModel=new ViewModelProvider(getActivity()).get(OrderViewModel.class);

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_rank_passenger, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 显示尾号
        String phone=mOrderViewModel.getOrder().getValue().getPassengerPhoneNumber();
        mBinding.textViewDriverOrderRankPassengerNumber.setText(phone.substring(phone.length()-4));

        // 获取账单总额
        final Bundle bundle=getArguments();
        double total=bundle.getDouble(Constant.EXTRA_TOTAL);
        mBinding.textViewDriverOrderRankPassengerAmount.setText(String.valueOf(total));

        //设置滑动监听器
        mBinding.slideButtonRankPassenger.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                // 进行评分
                long orderId=mOrderViewModel.getOrder().getValue().getOrderId();
                double rate=mBinding.ratingBarDriverOrderRankPassenger.getRating();
                new Thread(new RateForPassengerTask(orderId,rate,handler)).start();
            }
        });
        // 填充布局
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 语音播报信息
        mTtsUtility.speaking("订单已结束，请评价乘客。");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof OnToolbarListener){
            // 改变标题栏标题
            ((OnToolbarListener)getActivity()).setTitle(getString(R.string.title_order_finish));
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
            String message=bundle.getString(Constant.EXTRA_RESPONSE_MESSAGE);

            if(isSuccessful){
                Toast.makeText(getContext(), getString(R.string.hint_confirm_successful), Toast.LENGTH_SHORT).show();

                // 填充数据
                Intent intent=new Intent();
                double total=Double.parseDouble(mBinding.textViewDriverOrderRankPassengerAmount.getText().toString());
                bundle.putDouble(Constant.EXTRA_TOTAL,total);
                intent.putExtras(bundle);
                getActivity().setResult(Activity.RESULT_OK,intent);

                // 跳转首页
                getActivity().finish();
            }else{
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    });
}