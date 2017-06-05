package com.showjoy.weex;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.showjoy.android.rxbus.RxBus;
import com.showjoy.android.storage.SHStorageManager;
import com.showjoy.weex.event.LoadingEvent;
import com.showjoy.weex.event.NewPageEvent;
import com.showjoy.weex.event.ShowTitleBarEvent;
import com.showjoy.weex.event.TitleEvent;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.IWXDebugProxy;
import com.taobao.weex.common.WXRenderStrategy;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rx.Subscription;

import static com.showjoy.weex.SHWeexConstants.WEEX;

/**
 * Created by lufei on 5/24/17.
 */

public class SHWeexRender implements IWXRenderListener {

    Context context;
    Activity activity;
    Bundle bundle;

    ISHWeexRenderCallback weexRenderCallback;

    Map<String, WXSDKInstance> wxsdkInstanceMap = new HashMap<>();
    WXSDKInstance wxInstance;
    Map<String, View> viewMap = new HashMap<>();

    private String h5Url;

    final int LOAD_LOCAL_FILE = 1;

    private String weexJsUrl;
    private Map<String, Object> options;

    BroadcastReceiver broadcastReceiver;

    Subscription showTitleBarSubscription;
    Subscription titleSubscription;
    Subscription loadingSubscription;
    Subscription newPageSubscription;
    Subscription shareInfoSubscription;

    long startTime = 0;

    File weexDir;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_LOCAL_FILE:
                    if (wxInstance != null) {
                        String content = (String) msg.obj;
                        if (TextUtils.isEmpty(content)) {
                            SHWeexManager.get().getService().openUrl(activity, h5Url, true);
                            finishActivity();
                        } else {
                            wxInstance.render((String) msg.obj, options, null);
                        }
                    }
                    break;
            }
        }
    };

    public SHWeexRender(Activity activity, Bundle bundle, ISHWeexRenderCallback weexRenderCallback) {

        context = activity.getApplicationContext();
        this.activity = activity;
        weexDir = context.getDir(WEEX, Context.MODE_PRIVATE);

        this.bundle = bundle;
        this.weexRenderCallback = weexRenderCallback;

        options = new HashMap<String, Object>();
        options.put("platform", "android");
        if (null != SHWeexManager.get().getService()) {
            options.put("version", SHWeexManager.get().getService().getVersion());
        }

        initSubscription();
        initWeex();
    }

    private boolean isInstanceValid(String instanceId) {
        if (null == wxInstance) {
            return false;
        }
        if (TextUtils.isEmpty(wxInstance.getInstanceId())) {
            return false;
        }
        if (wxInstance.getInstanceId().equals(instanceId)) {
            return true;
        }
        return false;
    }

    private void initSubscription() {
        if (null == showTitleBarSubscription) {
            showTitleBarSubscription = RxBus.getDefault().subscribe(ShowTitleBarEvent.class, showTitleBarEvent -> {
                if (!isInstanceValid(showTitleBarEvent.instanceId)) {
                    return;
                }
                if (null != weexRenderCallback) {
                    weexRenderCallback.showTitleBar(showTitleBarEvent.visible);
                }
            }, throwable -> {

            });
        }

        if (null == titleSubscription) {
            titleSubscription = RxBus.getDefault().subscribe(TitleEvent.class, titleEvent -> {
                if (!isInstanceValid(titleEvent.instanceId)) {
                    return;
                }
                if (null != weexRenderCallback) {
                    weexRenderCallback.setTitle(titleEvent.title);
                }
            }, throwable -> {

            });
        }

        if (null == loadingSubscription) {
            loadingSubscription = RxBus.getDefault().subscribe(LoadingEvent.class, loadingEvent -> {
                if (!isInstanceValid(loadingEvent.instanceId)) {
                    return;
                }
                if (null != weexRenderCallback) {
                    weexRenderCallback.showLoading(loadingEvent.visible, loadingEvent.title);
                }
            }, throwable -> {

            });
        }

        if (null == newPageSubscription) {
            newPageSubscription = RxBus.getDefault().subscribe(NewPageEvent.class, newPageEvent -> {
                if (!isInstanceValid(newPageEvent.instanceId)) {
                    return;
                }
                loadPage(newPageEvent.intent.getExtras());
            }, throwable -> {

            });
        }

    }

    private void initWeex() {
        if (null == viewMap) {
            viewMap = new HashMap<>();
        }
        if (null == wxsdkInstanceMap) {
            wxsdkInstanceMap = new HashMap<>();
        }

        wxInstance = new WXSDKInstance(activity);
        wxInstance.registerRenderListener(this);
        wxInstance.onActivityCreate();
        registerBroadcastReceiver();

        loadPage(bundle);
    }

    private void loadPage(Bundle bundle) {
        loadPage(bundle, false);
    }

    private void loadPage(Bundle bundle, boolean force) {
        if (null != bundle) {
            String url = bundle.getString(SHWeexConstants.EXTRA_URL);
            h5Url = bundle.getString(SHWeexConstants.EXTRA_H5);
            if (!TextUtils.isEmpty(h5Url)) {
                Uri uri = Uri.parse(h5Url);
                Set<String> params = uri.getQueryParameterNames();
                if (null != params && params.size() > 0) {
                    for (String param : params) {
                        options.put(param, uri.getQueryParameter(param));
                    }
                }
            }
            if (!TextUtils.isEmpty(url)) {
                weexJsUrl = url;
                if (force) {
                    renderPageForce(weexJsUrl, options);
                } else {
                    renderPage(weexJsUrl, options);
                }

            } else {
                SHWeexManager.get().getService().openUrl(activity, h5Url, true);
                finishActivity();
            }
        }
    }

    public void finishActivity() {
        if (null != this.activity) {
            this.activity.finish();
        }
    }

    protected void destroyWeexInstance(WXSDKInstance wxInstance) {
        if (wxInstance != null) {
            wxInstance.registerRenderListener(null);
            wxInstance.destroy();
            wxInstance = null;
        }
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        viewMap.put(weexJsUrl, view);
        if (null != weexRenderCallback) {
            weexRenderCallback.viewCreated(view, h5Url);
        }

    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
        if (null != weexRenderCallback) {
            weexRenderCallback.renderEnd(true, h5Url);
        }
    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {
    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {
        SHWeexLog.e("weex exception:" + errCode + msg);
        Map<String, String> params = new HashMap<>();
        SHWeexManager.get().getService().openUrl(activity, h5Url, true);

        params.put("url", h5Url); if (null != weexRenderCallback) {
            weexRenderCallback.renderEnd(false, h5Url);
        }
    }

    public WXSDKInstance getWxInstance() {
        return wxInstance;
    }

    public void onStart() {
        if (wxInstance != null) {
            wxInstance.onActivityStart();
        }
    }

    public void onResume() {
        if (wxInstance != null) {
            wxInstance.onActivityResume();

            String data = SHStorageManager.get(SHWeexConstants.WEEX, SHWeexConstants.DATA, "");

            if (!TextUtils.isEmpty(data)) {
                String name = SHStorageManager.get(SHWeexConstants.WEEX, SHWeexConstants.NAME, "");

                try {
                    JSONObject jsonObj = JSONObject.parseObject(data);
                    Map<String, Object> params = new HashMap<>();
                    for (Map.Entry<String, Object> entry : jsonObj.entrySet()) {
                        params.put(entry.getKey(), entry.getValue());
                    }
                    wxInstance.fireGlobalEventCallback(name, params);
                } catch (Exception e) {
                    SHWeexLog.e(e);
                } finally {
                    SHStorageManager.removeFromCache(SHWeexConstants.WEEX, SHWeexConstants.DATA);
                    SHStorageManager.removeFromCache(SHWeexConstants.WEEX, SHWeexConstants.NAME);
                }
            }
        }
    }

    public void onPause() {
        if (wxInstance != null) {
            wxInstance.onActivityPause();
        }
    }

    public void onStop() {
        if (wxInstance != null) {
            wxInstance.onActivityStop();
        }
    }

    public void onDestroy() {
        destroyWeexInstance(wxInstance);

        if (null != wxsdkInstanceMap) {
            wxInstance = null;
            for (Map.Entry<String, WXSDKInstance> entry : wxsdkInstanceMap.entrySet()) {
                destroyWeexInstance(entry.getValue());
            }
        }

        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
        unregisterBroadcastReceiver();
        destroySubscription(loadingSubscription);
        destroySubscription(showTitleBarSubscription);
        destroySubscription(titleSubscription);
        destroySubscription(newPageSubscription);
    }


    public void destroySubscription(Subscription subscription) {
        if (null != subscription && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }

    }


    public String getPageName() {
        return "weexPage";
    }

    public void renderPage(String url, Map options) {

        if (viewMap.containsKey(url)) {
            if (null != weexRenderCallback) {
                weexRenderCallback.viewCreated(viewMap.get(url), url);
            }
            wxInstance = wxsdkInstanceMap.get(url);
            return;
        }

        startTime = System.currentTimeMillis();

        wxInstance = wxsdkInstanceMap.get(url);

        if (null == wxInstance) {
            wxInstance = new WXSDKInstance(activity);
            wxInstance.registerRenderListener(this);
            wxInstance.onActivityCreate();
            wxsdkInstanceMap.put(url, wxInstance);
        }

        render(url, options);
    }

    public void renderPageForce(String url, Map options) {

        startTime = System.currentTimeMillis();

        wxInstance = new WXSDKInstance(activity);
        wxInstance.registerRenderListener(this);
        wxInstance.onActivityCreate();
        wxsdkInstanceMap.put(url, wxInstance);

        render(url, options);
    }

    private void render(String url, Map options) {
        weexJsUrl = url;
        this.options = options;

        if (url.startsWith("http")) {
            wxInstance.renderByUrl(
                    getPageName(),
                    url,
                    options,
                    null,
                    WXRenderStrategy.APPEND_ASYNC);


            if (null != weexRenderCallback) {
                weexRenderCallback.renderStart(true, h5Url);
            }
        } else {


            if (null != weexRenderCallback) {
                weexRenderCallback.renderStart(false, h5Url);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String file = SHWeexUtils.readFile(url);
                    handler.sendMessage(handler.obtainMessage(LOAD_LOCAL_FILE, file));
                }
            }).start();
        }
    }

    public class DefaultBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (IWXDebugProxy.ACTION_DEBUG_INSTANCE_REFRESH.equals(intent.getAction())) {
                renderPage(weexJsUrl, options);
            } else if (WXSDKEngine.JS_FRAMEWORK_RELOAD.equals(intent.getAction())) {
                renderPage(weexJsUrl, options);
            }
        }
    }

    public void registerBroadcastReceiver() {
        broadcastReceiver = new DefaultBroadcastReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(IWXDebugProxy.ACTION_DEBUG_INSTANCE_REFRESH);
        filter.addAction(WXSDKEngine.JS_FRAMEWORK_RELOAD);
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(broadcastReceiver, filter);

    }

    public void unregisterBroadcastReceiver() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(context)
                    .unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }
}