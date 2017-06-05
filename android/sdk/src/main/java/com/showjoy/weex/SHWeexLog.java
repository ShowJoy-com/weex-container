package com.showjoy.weex;

import android.util.Log;

/**
 * Created by lufei on 5/23/17.
 */

public class SHWeexLog {

    public static void e(String s) {
        SHWeexManager.get().getService().e(s);
    }

    public static void e(Throwable e) {
        SHWeexManager.get().getService().e(e);
    }

    public static void d(String tag, String e) {
        if (!SHWeexManager.get().getService().isRelease()) {
            Log.d(tag, e);
        }
    }

}
