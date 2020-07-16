/**
 * @Author 屠天宇
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */

package com.sosotaxi.driver.common;

public class ProgressRunnable implements Runnable{

    private boolean mStop = false;
    private int mCurrentProgress = 0;
    private int mTotalProgress = 100;
    private CircleProgressBar mCircleProgressBar;


    public ProgressRunnable(CircleProgressBar circleProgressBar) {
        this.mCircleProgressBar = circleProgressBar;
    }

    public boolean isStop() {
        return mStop;
    }

    public void setStop(boolean mStop) {
        this.mStop = mStop;
    }

    public void setCurrentProgress(int mCurrentProgress) {
        this.mCurrentProgress = mCurrentProgress;
        mCircleProgressBar.setProgress(mCurrentProgress);
    }

    @Override
    public void run() {
        while (!mStop) {
            if (mCurrentProgress >= mTotalProgress) {
                mCurrentProgress = 0;
            }
            mCurrentProgress += 1;
            mCircleProgressBar.setProgress(mCurrentProgress);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mCurrentProgress = 0;
        mCircleProgressBar.setProgress(mCurrentProgress);
    }
}
