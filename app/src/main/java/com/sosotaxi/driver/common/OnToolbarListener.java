/**
 * @Author 范承祥
 * @CreateTime 2020/7/18
 * @UpdateTime 2020/7/18
 */
package com.sosotaxi.driver.common;

/**
 * 工具栏监听器
 */
public interface OnToolbarListener {
    /**
     * 设置工具栏标题
     * @param title 标题
     */
    void setTitle(String title);

    /**
     * 设置工具栏是否展示
     * @param isShown 是否展示
     */
    void showToolbar(boolean isShown);

    /**
     * 设置工具栏返回按钮是否显示
     * @param isShown 是否展示
     */
    void showBackButton(boolean isShown);
}
