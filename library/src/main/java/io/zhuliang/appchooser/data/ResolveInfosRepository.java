package io.zhuliang.appchooser.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.annotation.NonNull;

import java.util.List;

import io.zhuliang.appchooser.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Zhu Liang
 */

public class ResolveInfosRepository implements ResolveInfosDataSource {

    private PackageManager mPackageManager;
    private BaseSchedulerProvider mSchedulerProvider;

    public ResolveInfosRepository(@NonNull Context context,
                                  @NonNull BaseSchedulerProvider schedulerProvider) {
        mPackageManager = context.getPackageManager();
        mSchedulerProvider = schedulerProvider;
    }

    @Override
    public Observable<List<ResolveInfo>> listIntentActivities(@NonNull final Intent intent) {
        return Observable
                .create(new Observable.OnSubscribe<List<ResolveInfo>>() {
                    @Override
                    public void call(Subscriber<? super List<ResolveInfo>> subscriber) {
                        List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(intent,
                                PackageManager.MATCH_DEFAULT_ONLY);
                        subscriber.onNext(resolveInfos);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }
}
