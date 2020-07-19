/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;

/**
 * 评价乘客界面
 */
public class RankPassengerFragment extends Fragment {

    /**
     * 语音播报对象
     */
    private TTSUtility mTtsUtility;

    private TextView mTextViewBillAmount;
    private TextView mTextViewNumber;
    private ImageView mImageViewAvatar;
    private RatingBar mRatingBar;
    private SlideButton mSlideButton;

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
        // 填充布局
        return inflater.inflate(R.layout.fragment_rank_passenger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 语音播报信息
        mTtsUtility.speaking("订单已结束，请评价乘客。");

        // 获取控件
        mTextViewBillAmount=getActivity().findViewById(R.id.textViewDriverOrderRankPassengerAmount);
        mTextViewNumber=getActivity().findViewById(R.id.textViewDriverOrderRankPassengerNumber);
        mImageViewAvatar=getActivity().findViewById(R.id.imageViewDriverOrderRankPassengerAvatar);
        mRatingBar=getActivity().findViewById(R.id.ratingBarDriverOrderRankPassenger);
        mSlideButton=getActivity().findViewById(R.id.slideButtonRankPassenger);

        // 获取账单总额
        Bundle bundle=getArguments();
        double total=bundle.getDouble(Constant.EXTRA_TOTAL);
        mTextViewBillAmount.setText(String.valueOf(total));

        //设置滑动监听器
        mSlideButton.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                Toast.makeText(getContext(), "确认成功!", Toast.LENGTH_SHORT).show();
                // 跳转首页
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() instanceof OnToolbarListener){
            // 改变标题栏标题
            ((OnToolbarListener)getActivity()).setTitle(getString(R.string.title_order_finish));
        }
    }
}