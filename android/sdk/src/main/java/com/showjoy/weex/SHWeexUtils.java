package com.showjoy.weex;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.showjoy.android.storage.SHStorageManager;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.util.List;
import java.util.Set;

/**
 * Created by lufei on 11/17/16.
 */

public class SHWeexUtils {

    public static String bundle2Json(Bundle bundle) {

        JSONObject jsonObject = new JSONObject();
        if (null == bundle) {
            return jsonObject.toString();
        }
        Set<String> keySet = bundle.keySet();
        if (null == keySet) {
            return jsonObject.toString();
        }
        for(String key : keySet){
            jsonObject.put(key, bundle.get(key).toString());
        }
        return jsonObject.toString();
    }

    public static String readFile(String fileName) {
        String res="";
        try{
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte [] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;

    }

    public static void openAppWithPackageName(Activity activity, String packagename) {

        PackageInfo packageinfo = null;
        try {
            packageinfo = activity.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        List<ResolveInfo> resolveinfoList = activity.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            String packageName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            activity.startActivity(intent);
        }
    }

    public static void openUrl(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("//")) {
            if (SHStorageManager.get("APP", "https", true)) {
                url = "https:" + url;
            }else {
                url = "http:" + url;
            }
        }else if (!url.contains("://")) {
            if (url.startsWith("/")) {
                url = url.substring(1);
            }
            url = SHWeexManager.get().getService().getDefaultHost() + url;
        }

        SHWeexManager.get().getService().openUrl(context, url, false);
    }
}
