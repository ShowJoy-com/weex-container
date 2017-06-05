package com.showjoy.weex;

import android.view.View;

/**
 * Created by lufei on 5/24/17.
 */

public interface ISHWeexRenderCallback {

    /**
     * 显示/隐藏titleBar
     * @param visible
     */
    void showTitleBar(boolean visible);

    /**
     * 设置title
     * @param title
     */
    void setTitle(String title);

    /**
     * 显示loading，之所以要注入这个接口，为了weex页面的loading与其他页面保持统一
     * @param visible
     * @param title
     */
    void showLoading(boolean visible, String title);

    /**
     * render开始
     * @param remote true, 远程链接渲染; false, 本地文件渲染
     */
    void renderStart(boolean remote, String h5Url);

    /**
     * 渲染结束
     * @param success true, 渲染成功; false, 渲染失败
     */
    void renderEnd(boolean success, String h5Url);

    /**
     * 渲染成功，返回View
     * @param view
     * @param h5Url
     */
    void viewCreated(View view, String h5Url);
}
