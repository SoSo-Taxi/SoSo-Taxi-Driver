/**
 * @Author 屠天宇
 * @CreateTime 2020/7/14
 * @UpdateTime 2020/7/15
 */
package com.sosotaxi.driver.ui.home;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.adapter.UndoneOrderRecycleViewAdapter;
import com.sosotaxi.driver.common.CircleProgressBar;
import com.sosotaxi.driver.common.ProgressRunnable;

import java.net.SocketTimeoutException;

public class HomeFragment extends Fragment{

    private HomeViewModel mViewModel;

    //司机评分
    private TextView mServicePointTextView;
    //司机流水
    private TextView mAccountTextView;
    //司机接单数
    private TextView mReceivingOrderQuantityTextView;
    //司机在线时长
    private TextView mOnlineTimeTextView;

    //未完成订单数目
    private TextView mUndoneOrderQuantityTextView;
    private RecyclerView mUndoneOrderRecycleView;
    private UndoneOrderRecycleViewAdapter mUndoneOrderRecycleViewAdapter;

    //模拟的出发地和目的地
    private String[] startingPoints = {"出发点1","出发点2","出发点3","出发点4"};
    private String[] destinations = {"目的地1","目的地2","目的地3","目的地4"};

    //听单动态效果
    private CircleProgressBar mCircleProgressBar;
    private TextView mStartOrderTextView;
    private Thread mDrawingCircleThread;
    private ProgressRunnable mProgressRunnable;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mServicePointTextView = root.findViewById(R.id.firstpage_servicepoint_textView);
        mAccountTextView = root.findViewById(R.id.firstpage_account_textView);
        mReceivingOrderQuantityTextView = root.findViewById(R.id.firstpage_orderquantity_textView);


        mOnlineTimeTextView = root.findViewById(R.id.firstpage_onlinetime_textView);

        mUndoneOrderQuantityTextView = root.findViewById(R.id.firstpage_undone_orderquantity_textView);

        mUndoneOrderRecycleView = root.findViewById(R.id.first_page_undone_order_recycleView);
        mUndoneOrderRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mUndoneOrderRecycleViewAdapter = new UndoneOrderRecycleViewAdapter(getContext());
        mUndoneOrderRecycleViewAdapter = new UndoneOrderRecycleViewAdapter(getContext(),startingPoints,destinations);
        mUndoneOrderRecycleView.setAdapter(mUndoneOrderRecycleViewAdapter);

        mUndoneOrderQuantityTextView.setText(String.valueOf(mUndoneOrderRecycleViewAdapter.getItemCount()));

        mCircleProgressBar = root.findViewById(R.id.circle_progress_bar);
        mStartOrderTextView = root.findViewById(R.id.start_order_textView);
        mProgressRunnable = new ProgressRunnable(mCircleProgressBar);
        mDrawingCircleThread = new Thread(mProgressRunnable);
        mStartOrderTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mDrawingCircleThread.start();
                        break;
                    case  MotionEvent.ACTION_UP:
                        if (mProgressRunnable.isStop()){
                            Toast.makeText(getActivity().getApplicationContext(),"转到接单界面",Toast.LENGTH_LONG).show();
                            mProgressRunnable.setCurrentProgress(0);
                        }else {
                            mProgressRunnable.setStop(true);
                        }
                        mProgressRunnable = new ProgressRunnable(mCircleProgressBar);
                        mDrawingCircleThread = new Thread(mProgressRunnable);
                        break;
                }
                return true;
            }
        });

        return root;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

}