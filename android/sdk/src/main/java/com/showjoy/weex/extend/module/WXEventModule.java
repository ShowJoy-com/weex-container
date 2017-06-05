package com.showjoy.weex.extend.module;

import com.showjoy.weex.SHWeexUtils;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;


public class WXEventModule extends WXModule {

    @JSMethod(uiThread = true)
    public void openURL(String url) {
        SHWeexUtils.openUrl(mWXSDKInstance.getContext(), url);
    }
}
