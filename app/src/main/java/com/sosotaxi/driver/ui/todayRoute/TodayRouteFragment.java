package com.sosotaxi.driver.ui.todayRoute;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sosotaxi.driver.R;


public class TodayRouteFragment extends Fragment {

    private TodayRouteViewModel mViewModel;

    public static TodayRouteFragment newInstance() {
        return new TodayRouteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today_route, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TodayRouteViewModel.class);
        // TODO: Use the ViewModel
    }

}