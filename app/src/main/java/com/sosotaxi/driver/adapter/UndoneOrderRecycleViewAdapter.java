/**
 * @Author 屠天宇
 * @CreateTime 2020/7/14
 * @UpdateTime 2020/7/19
 */

package com.sosotaxi.driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sosotaxi.driver.R;
import com.sosotaxi.driver.common.Constant;
import com.sosotaxi.driver.model.Driver;
import com.sosotaxi.driver.model.DriverVo;
import com.sosotaxi.driver.model.Order;
import com.sosotaxi.driver.model.message.AskForDriverBody;
import com.sosotaxi.driver.model.message.BaseMessage;
import com.sosotaxi.driver.model.message.MessageType;
import com.sosotaxi.driver.ui.driverOrder.DriverOrderActivity;
import com.sosotaxi.driver.ui.home.AdapterListener;
import com.sosotaxi.driver.ui.home.HomeFragment;

import java.util.List;
import com.sosotaxi.driver.ui.home.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class UndoneOrderRecycleViewAdapter extends RecyclerView.Adapter<UndoneOrderRecycleViewAdapter.ViewHolder> {

    private Context mContext;

    private List<String> mStartingPoints;
    private List<String> mDestinations;
    private List<String> mScheduleTime;


    public UndoneOrderRecycleViewAdapter(Context context){
        this.mContext = context;
    }

    public UndoneOrderRecycleViewAdapter(Context context, List<String> startingPoints, List<String> destinations){
        this.mContext = context;
        this.mStartingPoints = startingPoints;
        this.mDestinations = destinations;
    }

    public UndoneOrderRecycleViewAdapter(Context mContext, List<String> mStartingPoints, List<String> mDestinations, List<String> mScheduleTime) {
        this.mContext = mContext;
        this.mStartingPoints = mStartingPoints;
        this.mDestinations = mDestinations;
        this.mScheduleTime = mScheduleTime;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_undone_order_recycleview,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.mStartingPointTextView.setText(mStartingPoints.get(position));
        holder.mDestinationTextView.setText(mDestinations.get(position));
        holder.mScheduleTimeTextView.setText(mScheduleTime.get(position));
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"还未到指定预约时间，无法查看预约订单详情",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        //有订单
        if(mStartingPoints.size() == mDestinations.size() && mStartingPoints.size() != 0){
            return mStartingPoints.size();
        }
        //无订单
            return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private TextView mStartingPointTextView;
        private TextView mDestinationTextView;
        private TextView mScheduleTimeTextView;
        private ConstraintLayout mConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.undone_order_CardView);
            mStartingPointTextView = itemView.findViewById(R.id.undone_order_starting_point);
            mDestinationTextView = itemView.findViewById(R.id.undone_order_destination_textView);
            mScheduleTimeTextView = itemView.findViewById(R.id.undone_order_time_textview);
            mConstraintLayout = itemView.findViewById(R.id.undone_order_constaint_layout);
        }
    }


}
