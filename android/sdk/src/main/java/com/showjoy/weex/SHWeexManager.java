package com.showjoy.weex;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.showjoy.android.storage.SHStorageManager;
import com.showjoy.weex.commons.adapter.SHCustomURIAdapter;
import com.showjoy.weex.commons.adapter.SHFrescoImageAdapter;
import com.showjoy.weex.commons.adapter.SHOkHttpAdapter;
import com.showjoy.weex.commons.adapter.SHStorageAdapter;
import com.showjoy.weex.commons.adapter.SHUserTrackAdapter;
import com.showjoy.weex.commons.util.DownLoaderTask;
import com.showjoy.weex.commons.util.JsonUtils;
import com.showjoy.weex.commons.util.NetWorkUtils;
import com.showjoy.weex.entities.WeexConfig;
import com.showjoy.weex.extend.component.FrescoImageComponent;
import com.showjoy.weex.extend.module.SHWXBaseModule;
import com.showjoy.weex.extend.module.WXEventModule;
import com.taobao.weex.InitConfig;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;
import com.taobao.weex.utils.WXJsonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.Dns;

import static com.showjoy.weex.SHWeexConstants.WEEX;

/**
 * Created by lufei on 10/28/16.
 */

public class SHWeexManager {

    static SHWeexManager sWeexManager = new SHWeexManager();
    final String EXTRA_URL = "url";
    final String EXTRA_TITLE_BAR = "titleBar";

    ISHWeexService weexService;
    List<WeexConfig> configList;


    String WEEX_CONFIG = "weex_config";
    String WEEX_FILE_MODIFIED_TIME = "weex_modified";

    String CONFIG_WEEX_ENABLE = "weexEnable";

    Context context;

    File weexDir;

    private SHWeexManager() {
    }

    public static SHWeexManager get() {
        return sWeexManager;
    }

    public void init(Application application, ISHWeexService weexService) {

        context = application.getApplicationContext();
        weexDir = context.getDir(WEEX, Context.MODE_PRIVATE);

        //根据需要注册图片、网络、存储等adapter
        WXSDKEngine.initialize(application,
                new InitConfig.Builder()
                        .setImgAdapter(new SHFrescoImageAdapter())
                        .setUtAdapter(new SHUserTrackAdapter())
                        .setHttpAdapter(new SHOkHttpAdapter())
                        .setStorageAdapter(new SHStorageAdapter())
                        .setURIAdapter(new SHCustomURIAdapter())
                        .build());

        this.weexService = weexService;

        //获取本地缓存的weex js配置
        configList = WXJsonUtils.getList(SHStorageManager.get(WEEX, WEEX_CONFIG, ""), WeexConfig.class);
        update();

        try {
            //基础接口
            WXSDKEngine.registerModule("shBase", SHWXBaseModule.class);
            //主要是a标签的跳转
            WXSDKEngine.registerModule("event", WXEventModule.class);
            //用fresco重写图片组件
            WXSDKEngine.registerComponent("image", FrescoImageComponent.class);
        } catch (WXException e) {
            SHWeexLog.e(e);
        }

        requestConfig();

    }

    public void setDebug(boolean enable, String host) {
        WXEnvironment.sRemoteDebugMode = enable;
        WXEnvironment.sRemoteDebugProxyUrl = "ws://" + host + ":8088/debugProxy/native";
    }

    public ISHWeexService getService() {
        if (null == weexService) {
            weexService = new ISHWeexService() {

                @Override
                public void openUrl(Context activity, String url, boolean force) {

                }

                @Override
                public void requestWeexConfig(ISHWeexRequestCallback weexRequestCallback) {

                }

                @Override
                public boolean isRelease() {
                    return false;
                }

                @Override
                public String getVersion() {
                    return "";
                }

                @Override
                public String getDefaultHost() {
                    return "";
                }

                @Override
                public void setCookie(Cookie cookie) {

                }

                @Override
                public List<Cookie> getAllCookies() {
                    return null;
                }

                @Override
                public Dns getDns() {
                    return null;
                }

                @Override
                public void e(String e) {

                }

                @Override
                public void e(Throwable e) {

                }

                @Override
                public Intent getWeexIntent() {
                    return null;
                }

                @Override
                public boolean isSupportHttps() {
                    return false;
                }

                @Override
                public void onEvent(String key, Map<String, Serializable> params) {

                }
            };
        }
        return weexService;
    }

    public void update() {

        if (null == configList) {
            return;
        }
        for (WeexConfig config : configList) {
            if (config.url.startsWith("//")) {
                if (SHWeexManager.get().getService().isSupportHttps()) {
                    config.url = "https:" + config.url;
                }else {
                    config.url = "http:" + config.url;
                }
            }
        }

        final List<WeexConfig> lastConfigList = new ArrayList<>();
        if (null != configList) {
            lastConfigList.addAll(configList);
        }

        for (WeexConfig weexConfig : lastConfigList) {
            String fileName = weexConfig.page + ".js";
            File f = new File(weexDir, fileName);
            if (f.exists()) {
                if (TextUtils.isEmpty(weexConfig.md5)) {
                    f.delete();
                }else {
                    if (weexConfig.md5.equals(getFileMD5(f))) {
                        SHStorageManager.putToDisk(WEEX, WEEX_FILE_MODIFIED_TIME + weexConfig.page, f.lastModified());
                        continue;
                    } else {
                        f.delete();
                    }
                }
            }
            if (!NetWorkUtils.isNetworkAvailable(context)) {
                break;
            }
            new DownLoaderTask(weexConfig.url, weexDir.getAbsolutePath(), fileName, new DownLoaderTask.DownloadCallBack() {
                @Override
                public void onPostExecute() {
                    File file = new File(weexDir, fileName);
                    if (TextUtils.isEmpty(weexConfig.md5)) {
                        SHStorageManager.putToDisk(WEEX, WEEX_FILE_MODIFIED_TIME + weexConfig.page, file.lastModified());
                    }else {
                        if (!weexConfig.md5.equals(getFileMD5(file))) {
                            file.delete();
                        } else {
                            SHStorageManager.putToDisk(WEEX, WEEX_FILE_MODIFIED_TIME + weexConfig.page, file.lastModified());
                        }
                    }
                }
            }).execute();
        }

    }

    public void requestConfig() {
        SHWeexManager.get().getService().requestWeexConfig(new ISHWeexRequestCallback() {
            @Override
            public void response(List<WeexConfig> weexConfigs) {
                SHStorageManager.putToDisk(WEEX, WEEX_CONFIG, JsonUtils.toJson(weexConfigs));
                configList = weexConfigs;
                update();
            }
        });
    }

    public Intent getIntentByUrl(String url) {

        if (null == configList) {
            return null;
        }
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String noSchemaUrl = url.replace("https:", "").replace("http:", "");
        if (noSchemaUrl.contains("?")) {
            int index = noSchemaUrl.indexOf("?");
            noSchemaUrl = noSchemaUrl.substring(0, index);
        }

        Intent intent;

        for (WeexConfig weexConfig : configList) {
            if (weexConfig.h5.contains(noSchemaUrl)) {
                intent = getIntent(url, weexConfig);
                if (null != intent) {
                    return intent;
                }
            }
        }
        return null;
    }

    private Intent getIntent(String h5, WeexConfig weexConfig) {
        if (!checkVersionEnable(weexConfig.v)) {
            return null;
        }
        Intent intent = SHWeexManager.get().getService().getWeexIntent();

        String fileName = weexConfig.page + ".js";
        File file = new File(weexDir, fileName);
        if (file.exists() && file.lastModified() == SHStorageManager.get(WEEX, WEEX_FILE_MODIFIED_TIME + weexConfig.page, 0l)) {

            intent.putExtra(EXTRA_URL, file.getAbsolutePath());
        } else {
            intent.putExtra(EXTRA_URL, weexConfig.url);
            update();
        }
        intent.putExtra(SHWeexConstants.EXTRA_H5, h5);
        if ("true".equals(weexConfig.hideTitleBar)) {
            intent.putExtra(EXTRA_TITLE_BAR, false);
        }
        return intent;
    }

    public Intent getIntent(String page) {

        if (null == configList) {
            return null;
        }
        for (WeexConfig weexConfig : configList) {
            if (weexConfig.page.equals(page)) {
                if (!checkVersionEnable(weexConfig.v)) {
                    continue;
                }
                Intent intent = SHWeexManager.get().getService().getWeexIntent();

                String fileName = weexConfig.page + ".js";
                File file = new File(weexDir, fileName);
                if (file.exists() && file.lastModified() == SHStorageManager.get(WEEX, WEEX_FILE_MODIFIED_TIME + weexConfig.page, 0l)) {

                    intent.putExtra(EXTRA_URL, file.getAbsolutePath());
                } else {
                    intent.putExtra(EXTRA_URL, weexConfig.url);
                    update();
                }
                intent.putExtra(EXTRA_URL, weexConfig.url);
                intent.putExtra(SHWeexConstants.EXTRA_H5, weexConfig.h5);
                return intent;
            }
        }
        return null;
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

    private static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    private boolean checkVersionEnable(String versionName) {
        String currentVersionName = SHWeexManager.get().getService().getVersion();
        String[] currentVersionArray = currentVersionName.split(".");
        String[] versionArray = versionName.split(".");
        for (int i = 0; i < currentVersionArray.length; i++) {
            try {
                if (Integer.parseInt(currentVersionArray[i]) > Integer.parseInt(versionArray[i])) {
                    return true;
                } else if (Integer.parseInt(currentVersionArray[i]) < Integer.parseInt(versionArray[i])) {
                    return false;
                }
            } catch (Exception e) {
                SHWeexLog.e(e);
                return false;
            }
        }
        return true;
    }

    public class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (null != info) {
                if (info.isConnected()) {
                    update();
                }
            }
        }
    }

}
