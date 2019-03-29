package io.zhuliang.appchooser.data;

import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

import androidx.annotation.NonNull;
import rx.Observable;

/**
 * @author Zhu Liang
 */

public interface ResolveInfosDataSource {

    @Deprecated
    Observable<List<ResolveInfo>> listIntentActivities(@NonNull Intent intent);

    @NonNull
    List<ResolveInfo> listResolveInfos(@NonNull Intent intent);
}
