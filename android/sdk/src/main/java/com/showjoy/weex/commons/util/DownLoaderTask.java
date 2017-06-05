package com.showjoy.weex.commons.util;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.showjoy.weex.SHWeexLog;
import com.showjoy.weex.SHWeexManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {
    private final String TAG = "DownLoaderTask";
    private URL mUrl;
    private String originalUrl;
    private File mFile;
    private int mProgress = 0;
    private ProgressReportingOutputStream mOutputStream;
    private DownloadCallBack downloadCallBack;

    private static IHttpDNS sHttpDNS;

    public interface IHttpDNS {
        String getIp(String host);
    }

    public DownLoaderTask(String url, String out, String fileName, DownloadCallBack downloadCallBack) {
        super();
        try {
            originalUrl = url;
            mUrl = new URL(url);
            mFile = new File(out, fileName);
        } catch (MalformedURLException e) {
            SHWeexManager.get().getService().e(e);
        }
        this.downloadCallBack = downloadCallBack;

    }

    public static void setHttpDNS(IHttpDNS httpDNS) {
        sHttpDNS = httpDNS;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Long doInBackground(Void... params) {
        return download();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    //下载保存后执行  
    @Override
    protected void onPostExecute(Long result) {
        if (isCancelled())
            return;
        if (null != downloadCallBack) {
            downloadCallBack.onPostExecute();
        }
    }

    private long download() {
        URLConnection connection = null;
        int bytesCopied = 0;

        if (mUrl == null) {
            SHWeexLog.e("DownLoaderTask -> long download() -> mUrl is NULL");
            return 0L;
        }

        try {
            if (null != sHttpDNS) {
                String ip = sHttpDNS.getIp(mUrl.getHost());
                if (!TextUtils.isEmpty(ip)) {
                    String newUrl = originalUrl.replaceFirst(mUrl.getHost(), ip);
                    connection = new URL(newUrl).openConnection();
                    connection.setRequestProperty("Host", mUrl.getHost());
                }
            }
            if (null == connection) {
                connection = mUrl.openConnection();
            }
            int length = connection.getContentLength();
            if (mFile.exists() && length == mFile.length()) {
                SHWeexLog.d(TAG, "file " + mFile.getName() + " already exits!!");
                return 0L;
            }
            mOutputStream = new ProgressReportingOutputStream(mFile);
            publishProgress(0, length);
            bytesCopied = copy(connection.getInputStream(), mOutputStream);
            if (bytesCopied != length && length != -1) {
                Log.e(TAG, "Download incomplete bytesCopied=" + bytesCopied + ", length" + length);
            }
            mOutputStream.close();
        } catch (Exception e) {
            SHWeexLog.e(e);
        }
        return bytesCopied;
    }

    private int copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[1024 * 8];
        BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } catch (IOException e) {
            SHWeexLog.e(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                SHWeexLog.e(e);
            }
            try {
                in.close();
            } catch (IOException e) {
                SHWeexLog.e(e);
            }
        }
        return count;
    }

    private final class ProgressReportingOutputStream extends FileOutputStream {

        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
        }

        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            super.write(buffer, byteOffset, byteCount);
            mProgress += byteCount;
            publishProgress(mProgress);
        }

    }

    public interface DownloadCallBack {
        void onPostExecute();
    }
} 