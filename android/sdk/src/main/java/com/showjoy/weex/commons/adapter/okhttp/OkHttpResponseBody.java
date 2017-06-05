package com.showjoy.weex.commons.adapter.okhttp;

import com.showjoy.weex.commons.adapter.okhttp.listener.OkHttpResponseListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by lufei on 11/17/16.
 */
public class OkHttpResponseBody extends ResponseBody {

    private ResponseBody realBody;
    private OkHttpResponseListener responseListener;

    public OkHttpResponseBody(ResponseBody realBody, OkHttpResponseListener responseListener) {
        this.realBody = realBody;
        this.responseListener = responseListener;
    }

    @Override
    public MediaType contentType() {
        return realBody.contentType();
    }

    @Override
    public long contentLength() {
        return realBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        return Okio.buffer(source(realBody.source()));
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {
            long totalConsumed = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long currentConsumed = super.read(sink, byteCount);
                totalConsumed += currentConsumed != -1 ? currentConsumed : 0;
                responseListener.onResponse(totalConsumed, OkHttpResponseBody.this.contentLength(), currentConsumed == -1);
                return currentConsumed;
            }
        };
    }
}