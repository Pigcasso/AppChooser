package io.zhuliang.appchooser.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * <pre>
 *     author : ZhuLiang
 *     time   : 2019/03/30
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ToastUtils {
    public static void showToast(@NonNull Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
