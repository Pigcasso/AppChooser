package io.zhuliang.appchooser.data;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import androidx.annotation.NonNull;

import java.util.List;

import rx.Observable;

/**
 * @author Zhu Liang
 */

public interface ResolveInfosDataSource {

    Observable<List<ResolveInfo>> listIntentActivities(@NonNull Intent intent);
}
