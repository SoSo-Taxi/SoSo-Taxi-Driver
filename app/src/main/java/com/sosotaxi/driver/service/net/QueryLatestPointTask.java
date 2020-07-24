/**
 * @Author 范承祥
 * @CreateTime 2020/7/22
 * @UpdateTime 2020/7/23
 */
package com.sosotaxi.driver.service.net;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.track.OnTrackListener;
import com.sosotaxi.driver.utils.TraceHelper;

/**
 * 查询最新位置任务
 */
public class QueryLatestPointTask implements Runnable {

    /** 查询时间间隔 */
    private long mTimeInterval;
    /** 实体名 */
    private String mEntityName;
    /** 轨迹监听器 */
    private OnTrackListener mOnTrackListener;
    /** 轨迹连接器 */
    private LBSTraceClient mTraceClient;
    /**
     * 退出标志位
     */
    private boolean mIsExit;

    public QueryLatestPointTask(long timeInterval, String entityName, OnTrackListener onTrackListener){
        mTimeInterval=timeInterval;
        mEntityName=entityName;
        mOnTrackListener=onTrackListener;
        mIsExit=false;
    }

    @Override
    public void run() {
        while (mIsExit==false){
            try {
                Thread.sleep(mTimeInterval);
                // 获取轨迹客户端
                mTraceClient=TraceHelper.getTraceClient();
                // 查询最新位置
                mTraceClient.queryLatestPoint(TraceHelper.buildLatestPointRequest(mEntityName),mOnTrackListener);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public Boolean siExit() {
        return mIsExit;
    }

    public void setIsExit(boolean isExit) {
        this.mIsExit = isExit;
    }
}
