package com.showjoy.weex.commons.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.showjoy.weex.SHWeexManager;
import com.taobao.weex.adapter.IWXUserTrackAdapter;
import com.taobao.weex.common.WXPerformance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by lufei on 11/17/16.
 */
public class SHUserTrackAdapter implements IWXUserTrackAdapter {
    @Override
    public void commit(Context context, String eventId, String type, WXPerformance perf, Map<String, Serializable> params) {
        if (!TextUtils.isEmpty(eventId)) {
            SHWeexManager.get().getService().onEvent(eventId, params);
        }
    }
}
