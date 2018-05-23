package io.zhuliang.appchooser.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/25 上午10:34
 */

public class ResolversRepository {

    private PackageManager mPackageManager;

    public ResolversRepository(Context context) {
        mPackageManager = context.getPackageManager();
    }

    public Observable<List<Resolver>> listResolvers(Intent intent) {
        return Observable
                .from(mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY))
                .flatMap(new Func1<ResolveInfo, Observable<Resolver>>() {
                    @Override
                    public Observable<Resolver> call(ResolveInfo resolveInfo) {
                        return Observable
                                .just(resolveInfo)
                                .map(new Func1<ResolveInfo, Resolver>() {
                                    @Override
                                    public Resolver call(ResolveInfo resolveInfo) {
                                        return new Resolver(resolveInfo);
                                    }
                                });
                    }
                })
                .toList();
    }
}
