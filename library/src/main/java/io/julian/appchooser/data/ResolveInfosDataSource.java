package io.julian.appchooser.data;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;

import java.util.List;

import rx.Observable;

/**
 * @author Zhu Liang
 */

public interface ResolveInfosDataSource {

    Observable<List<ResolveInfo>> listIntentActivities(@NonNull Intent intent);
}
