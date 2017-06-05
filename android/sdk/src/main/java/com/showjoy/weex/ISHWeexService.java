package com.showjoy.weex;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.Dns;

/**
 * Created by lufei on 11/15/16.
 */

public interface ISHWeexService {

    /**
     * 打开链接
     * @param activity
     * @param url
     * @param force  强制用h5打开，不会转成weex 或者native
     */
    void openUrl(Context activity, String url, boolean force);

    /**
     * 请求weex配置信息
     * @param weexRequestCallback
     */
    void requestWeexConfig(ISHWeexRequestCallback weexRequestCallback);

    /**
     * 判断是否是release，正式包
     * @return
     */
    boolean isRelease();

    /**
     * 获取app版本，用于判断weex 配置是否生效
     * weex的配置可是设置某个版本以上才支持
     * @return
     */
    String getVersion();

    /**
     * 获取默认的域名，用于支持相对地址
     * @return ， 如：  https://shop.m.showjoy.com/
     */
    String getDefaultHost();

    /**
     * 保存cookie
     * @param cookie
     */
    void setCookie(Cookie cookie);

    /**
     * 获取所有的cookie
     * @return
     */
    List<Cookie> getAllCookies();

    /**
     * 接入httpDNS, 如果没有接入，允许返回NULL
     * @return
     */
    Dns getDns();

    /**
     * 错误日志
     * @param e
     */
    void e(String e);
    void e(Throwable e);

    /**
     * 获取打开weex的intent
     * @return
     */
    Intent getWeexIntent();

    /**
     * 是否支持https
     * @return
     */
    boolean isSupportHttps();

    /**
     * 埋点事件
     * @param key
     * @param params
     */
    void onEvent(String key, Map<String, Serializable> params);

}
