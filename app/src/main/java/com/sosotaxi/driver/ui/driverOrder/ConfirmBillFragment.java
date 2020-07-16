/**
 * @Author 范承祥
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */
package com.sosotaxi.driver.ui.driverOrder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sosotaxi.driver.R;
import com.sosotaxi.driver.ui.widget.OnSlideListener;
import com.sosotaxi.driver.ui.widget.SlideButton;

/**
 *
 */
public class ConfirmBillFragment extends Fragment {

    private SlideButton mSlideButton;

    public ConfirmBillFragment() {
        // 所需空构造器
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 填充布局
        return inflater.inflate(R.layout.fragment_confirm_bill, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSlideButton=getActivity().findViewById(R.id.slideButtonConfirmBill);

        mSlideButton.addSlideListener(new OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                Toast.makeText(getContext(), "确认成功!", Toast.LENGTH_SHORT).show();
                // 跳转评价乘客界面
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                Fragment currentFragment=getActivity().getSupportFragmentManager().getFragments().get(0);
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.hide(currentFragment);
                fragmentTransaction.add(R.id.frameLayoutDriverOrder,new RankPassengerFragment(),null);
                fragmentTransaction.commit();
            }
        });
    }
}