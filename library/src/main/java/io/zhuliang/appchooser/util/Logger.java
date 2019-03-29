package io.zhuliang.appchooser.util;

import android.util.Log;

import io.zhuliang.appchooser.BuildConfig;

/**
 * <pre>
 *     author : ZhuLiang
 *     time   : 2019/03/30
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class Logger {

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
