/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/24
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.common.TTSUtility;
import com.sosotaxi.driver.databinding.FragmentArriveStartingPointBinding;
import com.sosotaxi.driver.databinding.FragmentConfirmBillBinding;
import com.sosotaxi.driver.model.message.ArriveDestPointBody;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;
import com.sosotaxi.driver.utils.MessageHelper;
import com.sosotaxi.driver.viewModel.OrderViewModel;

import java.util.Calendar;
import java.util.Date;

/**
 * 确认账单界面
 */
public class ConfirmBillFragment extends Fragment {

    /**
     * 订单ViewModel
     */
    private OrderViewModel mOrderViewModel;

    /**
     * 数据绑定对象
     */
    private FragmentConfirmBillBinding mBinding;

    /**
     * 语音播报对象
     */
    private TTSUtility mTtsUtility;

    /**
     * 消息帮手对象
     */
    private MessageHelper mMessageHelper;

    public ConfirmBillFragment() {
        // 获取语音播报对象
        mTtsUtility=TTSUtility.getInstance(getContext());
        // 获取消息帮助对象
        mMessageHelper=MessageHelper.getInstance();
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

        mBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_confirm_bill, container, false);
        mBinding.setViewModel(mOrderViewModel);
        mBinding.setLifecycleOwner(getActivity());

        // 设置滑动监听器
        mBinding.slideButtonConfirmBill.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                // 获取账单项金额
                double amount=0;
                double roadToll=0;
                double parkingRate=0;
                String amountString=mBinding.textViewDriverOrderBillAmount.getText().toString();
                String roadTollString=mBinding.editTextDriverOrderBillItemRoadToll.getText().toString();
                String parkingRateString=mBinding.editTextDriverOrderBillItemParkingRate.getText().toString();
                if(amountString.isEmpty()==false){
                    amount=Double.parseDouble(amountString);
                }
                if(roadTollString.isEmpty()==false){
                    roadToll=Double.parseDouble(roadTollString);
                }
                if(parkingRateString.isEmpty()==false){
                    parkingRate=Double.parseDouble(parkingRateString);
                }

                // 计算账单金额
                double total=amount+roadToll+parkingRate;

                // 填充数据
                Bundle bundle=new Bundle();
                bundle.putDouble(Constant.EXTRA_TOTAL,total);
                // 设置参数
                RankPassengerFragment rankPassengerFragment=new RankPassengerFragment();
                rankPassengerFragment.setArguments(bundle);

                // 封装消息
                ArriveDestPointBody body=new ArriveDestPointBody();
                body.setOrder(mOrderViewModel.getOrder().getValue());
                Calendar calendar=Calendar.getInstance();
                Date currentDate=calendar.getTime();
                body.setBasicCost(amount);
                body.setFreewayCost(roadToll);
                body.setParkingCost(parkingRate);
                body.getOrder().setArriveTime(currentDate);
                BaseMessage message=new BaseMessage(MessageType.ARRIVE_DEST_POINT_MESSAGE,body);

                //发送消息
                mMessageHelper.send(message);

                Toast.makeText(getContext(), getString(R.string.hint_confirm_successful), Toast.LENGTH_SHORT).show();

                // 跳转评价乘客界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,rankPassengerFragment,null);
                fragmentTransaction.commit();
            }
        });

        // 填充布局
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 语音播报信息
        mTtsUtility.speaking(getString(R.string.hint_finish_order_and_confirm_bill));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}