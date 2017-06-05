package com.showjoy.weex.commons.adapter;

import com.showjoy.weex.SHWeexLog;
import com.showjoy.weex.SHWeexManager;
import com.showjoy.weex.commons.adapter.okhttp.OkHttpRequestBody;
import com.showjoy.weex.commons.adapter.okhttp.OkHttpResponseBody;
import com.showjoy.weex.commons.adapter.okhttp.listener.OkHttpRequestListener;
import com.showjoy.weex.commons.adapter.okhttp.listener.OkHttpResponseListener;
import com.taobao.weex.adapter.IWXHttpAdapter;
import com.taobao.weex.common.WXRequest;
import com.taobao.weex.common.WXResponse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lufei on 11/17/16.
 */

public class SHOkHttpAdapter implements IWXHttpAdapter {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    public static final int REQUEST_FAILURE = -100;

    CookieJar cookieJar = new CookieJar() {
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                Iterator var3 = cookies.iterator();

                while (var3.hasNext()) {
                    Cookie item = (Cookie) var3.next();
                    SHWeexManager.get().getService().setCookie(item);
                }
            }

        }

        public List<Cookie> loadForRequest(HttpUrl url) {
            return SHWeexManager.get().getService().getAllCookies();
        }
    };

    @Override
    public void sendRequest(final WXRequest request, final OnHttpListener listener) {
        if (listener != null) {
            listener.onHttpStart();
        }

        if (!request.url.contains("://")) {
            request.url = SHWeexManager.get().getService().getDefaultHost() + request.url;
        }

        OkHttpRequestListener okHttpRequestListener = new OkHttpRequestListener() {
            @Override
            public void onRequest(long consumed, long total, boolean done) {
                if (null != listener) {
                    listener.onHttpUploadProgress((int) (consumed));
                }
            }
        };

        final OkHttpResponseListener responseListener = new OkHttpResponseListener() {
            @Override
            public void onResponse(long consumed, long total, boolean done) {
                if (null != listener) {
                    listener.onHttpResponseProgress((int) (consumed));
                }
            }
        };

        OkHttpClient.Builder OkHttpClientBuilder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new OkHttpResponseBody(originalResponse.body(), responseListener))
                        .build();
            }
        }).cookieJar(cookieJar);
        Dns dns = SHWeexManager.get().getService().getDns();
        if (null != dns) {
            OkHttpClientBuilder.dns(dns);
        }

         OkHttpClient client = OkHttpClientBuilder.build();

        if (METHOD_POST.equalsIgnoreCase(request.method)) {
            Request okHttpRequest = new Request.Builder()
                    .url(request.url)
                    .post(new OkHttpRequestBody(RequestBody.create(MediaType.parse(request.body), request.body), okHttpRequestListener))
                    .build();

            client.newCall(okHttpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    WXResponse wxResponse = new WXResponse();
                    wxResponse.errorCode = String.valueOf(REQUEST_FAILURE);
                    wxResponse.statusCode = String.valueOf(REQUEST_FAILURE);
                    wxResponse.errorMsg = e.getMessage();
                    try {
                        if (null != listener) {
                            listener.onHttpFinish(wxResponse);
                        }
                    } catch (Exception e1) {
                        SHWeexManager.get().getService().e(e1);
                    }

                    SHWeexLog.e(request.url + "请求失败");
                    SHWeexLog.e(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    WXResponse wxResponse = new WXResponse();
                    wxResponse.statusCode = String.valueOf(response.code());
                    if (requestSuccess(Integer.parseInt(wxResponse.statusCode))) {
                        wxResponse.originalData = response.body().bytes();
                    } else {
                        wxResponse.errorCode = String.valueOf(response.code());
                        wxResponse.errorMsg = response.body().string();
                    }

                    try {
                        if (null != listener) {
                            listener.onHttpFinish(wxResponse);
                        }
                    }catch (Exception e) {
                        SHWeexLog.e(e);
                    }

                }
            });
        } else {
            Request okHttpRequest = new Request.Builder().url(request.url).build();
            client.newCall(okHttpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    WXResponse wxResponse = new WXResponse();
                    wxResponse.errorCode = String.valueOf(REQUEST_FAILURE);
                    wxResponse.statusCode = String.valueOf(REQUEST_FAILURE);
                    wxResponse.errorMsg = e.getMessage();
                    try {
                        if (null != listener) {
                            listener.onHttpFinish(wxResponse);
                        }
                    } catch (Exception e1) {
                        SHWeexLog.e(e1);
                    }

                    SHWeexLog.e(request.url + "请求失败");
                    SHWeexLog.e(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    WXResponse wxResponse = new WXResponse();
                    wxResponse.statusCode = String.valueOf(response.code());
                    if (requestSuccess(Integer.parseInt(wxResponse.statusCode))) {
                        wxResponse.originalData = response.body().bytes();
                    } else {
                        wxResponse.errorCode = String.valueOf(response.code());
                        wxResponse.errorMsg = response.body().string();
                    }

                    try {
                        if (null != listener) {
                            listener.onHttpFinish(wxResponse);
                        }
                    } catch (Exception e) {
                        SHWeexLog.e(e);
                    }

                }
            });
        }
    }

    private boolean requestSuccess(int statusCode) {
        return statusCode >= 200 && statusCode <= 299;
    }

}
