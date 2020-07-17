package com.sosotaxi.driver.ui.driverOrder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.ui.main.MainActivity;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;

/**
 * 评价乘客界面
 */
public class RankPassengerFragment extends Fragment {

    private TextView mTextViewBillAmount;
    private TextView mTextViewNumber;
    private ImageView mImageViewAvatar;
    private RatingBar mRatingBar;
    private SlideButton mSlideButton;

    public RankPassengerFragment() {
        // 所需空构造器
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rank_passenger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle=getArguments();
        double total=bundle.getDouble(Constant.EXTRA_TOTAL);

        mTextViewBillAmount=getActivity().findViewById(R.id.textViewDriverOrderRankPassengerAmount);
        mTextViewNumber=getActivity().findViewById(R.id.textViewDriverOrderRankPassengerNumber);
        mImageViewAvatar=getActivity().findViewById(R.id.imageViewDriverOrderRankPassengerAvatar);
        mRatingBar=getActivity().findViewById(R.id.ratingBarDriverOrderRankPassenger);
        mSlideButton=getActivity().findViewById(R.id.slideButtonRankPassenger);

        mTextViewBillAmount.setText(String.valueOf(total));

        mSlideButton.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                Toast.makeText(getContext(), "确认成功!", Toast.LENGTH_SHORT).show();
                // 跳转首页
                Intent intent=new Intent(getContext(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }
}