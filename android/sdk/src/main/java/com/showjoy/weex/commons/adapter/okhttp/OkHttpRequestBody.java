package com.showjoy.weex.commons.adapter.okhttp;

import com.showjoy.weex.commons.adapter.okhttp.listener.OkHttpRequestListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by lufei on 11/17/16.
 */

public class OkHttpRequestBody extends RequestBody{

    private RequestBody realBody;
    private OkHttpRequestListener okHttpRequestListener;

    private BufferedSink bufferedSink;

    public OkHttpRequestBody(RequestBody realBody, OkHttpRequestListener okHttpRequestListener){
        this.realBody = realBody;
        this.okHttpRequestListener = okHttpRequestListener;
    }
    @Override
    public MediaType contentType() {
        return realBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return realBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //包装
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        realBody.writeTo(bufferedSink);
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long totalConsumed = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);

                totalConsumed += byteCount;

                okHttpRequestListener.onRequest(totalConsumed, contentLength(), totalConsumed == contentLength());
            }
        };
    }
}
