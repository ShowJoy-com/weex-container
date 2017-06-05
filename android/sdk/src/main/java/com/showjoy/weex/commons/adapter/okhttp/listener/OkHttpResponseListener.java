package com.showjoy.weex.commons.adapter.okhttp.listener;

/**
 * Created by lufei on 11/17/16.
 */

public interface OkHttpResponseListener {

    void onResponse(long consumed,long total,boolean done);
}
