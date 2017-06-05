package com.showjoy.weex.extend.module;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.showjoy.android.rxbus.RxBus;
import com.showjoy.android.storage.SHStorageManager;
import com.showjoy.weex.SHWeexConstants;
import com.showjoy.weex.SHWeexManager;
import com.showjoy.weex.event.LoadingEvent;
import com.showjoy.weex.event.NewPageEvent;
import com.showjoy.weex.event.ShowTitleBarEvent;
import com.showjoy.weex.event.TitleEvent;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

/**
 * Created by lufei on 5/25/17.
 */

public class SHWXBaseModule extends WXModule {

    @JSMethod(uiThread = true)
    public void loadPage(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (url.startsWith("//")) {
            if (SHStorageManager.get("APP", "https", true)) {
                url = "https:" + url;
            }else {
                url = "http:" + url;
            }
        }else if (!url.startsWith("http")){
            if (url.startsWith("/")) {
                url = url.substring(1);
            }
            url = SHWeexManager.get().getService().getDefaultHost() + url;
        }

        Intent intent = SHWeexManager.get().getIntentByUrl(url);
        if (null == intent) {
            SHWeexManager.get().getService().openUrl((Activity) mWXSDKInstance.getContext(), url, true);
        }else {
            RxBus.getDefault().post(new NewPageEvent(mWXSDKInstance.getInstanceId(), intent));
        }
    }

    @JSMethod(uiThread = true)
    public void showTitleBar(boolean visible) {
        RxBus.getDefault().post(new ShowTitleBarEvent(mWXSDKInstance.getInstanceId(), visible));
    }

    @JSMethod(uiThread = true)
    public void setTitle(String title) {
        RxBus.getDefault().post(new TitleEvent(mWXSDKInstance.getInstanceId(), title));
    }

    @JSMethod(uiThread = true)
    public void showLoading(String text) {
        RxBus.getDefault().post(new LoadingEvent(mWXSDKInstance.getInstanceId(), true, text));
    }

    @JSMethod(uiThread = true)
    public void showLoading() {
        RxBus.getDefault().post(new LoadingEvent(mWXSDKInstance.getInstanceId(), true, "加载中..."));
    }

    @JSMethod(uiThread = true)
    public void hideLoading() {
        RxBus.getDefault().post(new LoadingEvent(mWXSDKInstance.getInstanceId(), false, null));
    }

    @JSMethod
    public void fireGlobalEvent(String name, String data, final JSCallback callback) {
        SHStorageManager.putToCache(SHWeexConstants.WEEX, SHWeexConstants.NAME, name);
        SHStorageManager.putToCache(SHWeexConstants.WEEX, SHWeexConstants.DATA, data);

        if (null != callback) {
            callback.invoke(new JSONObject());
        }
    }
}
