/**
 * @Author 范承祥
 * @CreateTime 2020/7/14
 * @UpdateTime 2020/7/14
 */
package com.sosotaxi.driver.ui.widget;

/**
 * 滑动动作监听器接口
 */
public interface MotionListener {

    /**
     * 滑动时的回调
     * @param distanceX 滑动的X轴偏移量
     */
    void onActionMove(int distanceX);

    /**
     * 松开时的回调
     * @param x
     */
    void onActionUp(int x);
}
