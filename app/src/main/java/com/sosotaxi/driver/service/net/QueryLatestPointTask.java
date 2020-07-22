package com.sosotaxi.driver.service.net;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.track.OnTrackListener;
import com.sosotaxi.driver.utils.TraceHelper;

/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/22
 */
public class QueryLatestPointTask implements Runnable {

    private long mTimeInterval;
    private String mEntityName;
    private OnTrackListener mOnTrackListener;
    private LBSTraceClient mTraceClient;

    public QueryLatestPointTask(long timeInterval, String entityName, OnTrackListener onTrackListener){
        mTimeInterval=timeInterval;
        mEntityName=entityName;
        mOnTrackListener=onTrackListener;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(mTimeInterval);
                mTraceClient=TraceHelper.getTraceClient();
                mTraceClient.queryLatestPoint(TraceHelper.buildLatestPointRequest(mEntityName),mOnTrackListener);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
