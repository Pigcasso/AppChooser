package io.zhuliang.appchooser.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @author Zhu Liang
 */

public class ResolveInfosRepository implements ResolveInfosDataSource {

    private PackageManager mPackageManager;

    public ResolveInfosRepository(@NonNull Context context) {
        mPackageManager = context.getPackageManager();
    }

    @Override
    @NonNull
    public List<ResolveInfo> listResolveInfos(@NonNull Intent intent) {
        return mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    }
}
