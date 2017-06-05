package com.showjoy.weex.commons.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.showjoy.weex.SHWeexManager;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.adapter.URIAdapter;

/**
 * Created by lufei on 3/8/17.
 */

public class SHCustomURIAdapter implements URIAdapter {
    @NonNull
    @Override
    public Uri rewrite(WXSDKInstance instance, String type, Uri uri) {
        if (null == uri) {
            return null;
        }
        String url = uri.toString();
        if (url.startsWith("http")) {
            return uri;
        }else if (url.startsWith("//")) {
            if (SHWeexManager.get().getService().isSupportHttps()) {
                url = "https:" + url;
            }else {
                url = "http:" + url;
            }
        }else {
            url = SHWeexManager.get().getService().getDefaultHost() + url;
        }
        return Uri.parse(url);
    }
}
