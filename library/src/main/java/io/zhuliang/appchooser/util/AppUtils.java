package io.zhuliang.appchooser.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

import io.zhuliang.appchooser.action.ActionConfig;

/**
 * @author Du Wenyu
 * 2018/9/6
 */
public class AppUtils {

    public static void openWithRecommendApp(Context context, ActionConfig config) {
        Intent intent = new Intent(config.actionName);
        intent.setPackage(config.mRecommendApp.packageName);
        Uri uri = Uri.fromFile(new File(config.pathname));
        intent.setDataAndType(uri, config.mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static boolean isPackageInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void browse(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
    }
}
