package com.showjoy.weex.commons.adapter;

import com.showjoy.android.storage.SHStorageManager;
import com.showjoy.weex.SHWeexConstants;
import com.taobao.weex.appfram.storage.IWXStorageAdapter;
import com.taobao.weex.appfram.storage.StorageResultHandler;

import java.util.List;
import java.util.Map;

/**
 * Created by lufei on 11/24/16.
 */

public class SHStorageAdapter implements IWXStorageAdapter {

    @Override
    public void setItem(String key, String value, OnResultReceivedListener listener) {
        SHStorageManager.putToDisk(SHWeexConstants.WEEX, key, value);
        if (listener == null) {
            return;
        }
        Map<String, Object> data = StorageResultHandler.setItemResult(true);
        listener.onReceived(data);
    }

    @Override
    public void getItem(String key, OnResultReceivedListener listener) {
        if (listener == null) {
            return;
        }
        Map<String, Object> data = StorageResultHandler.getItemResult(SHStorageManager.get(SHWeexConstants.WEEX, key, ""));
        listener.onReceived(data);
    }

    @Override
    public void removeItem(String key, OnResultReceivedListener listener) {
        SHStorageManager.removeFromDisk(SHWeexConstants.WEEX, key);
        if (listener == null) {
            return;
        }
        Map<String, Object> data = StorageResultHandler.removeItemResult(true);
        listener.onReceived(data);
    }

    @Override
    public void length(OnResultReceivedListener listener) {
        List<String> keys = SHStorageManager.getAllKeys(SHWeexConstants.WEEX);
        long result = 0l;
        if (null != keys) {
            result = keys.size();
        }
        if (listener == null) {
            return;
        }
        Map<String, Object> data = StorageResultHandler.getLengthResult(result);
        listener.onReceived(data);
    }

    @Override
    public void getAllKeys(OnResultReceivedListener listener) {
        if (listener == null) {
            return;
        }
        List<String> keys = SHStorageManager.getAllKeys(SHWeexConstants.WEEX);
        long result = 0l;
        if (null != keys) {
            result = keys.size();
        }
        Map<String, Object> data = StorageResultHandler.getLengthResult(result);
        listener.onReceived(data);
    }

    @Override
    public void setItemPersistent(String key, String value, OnResultReceivedListener listener) {
        SHStorageManager.putToDisk(SHWeexConstants.WEEX, key, value);

        if (listener == null) {
            return;
        }
        Map<String, Object> data = StorageResultHandler.setItemResult(true);
        listener.onReceived(data);
    }

    @Override
    public void close() {

    }
}
