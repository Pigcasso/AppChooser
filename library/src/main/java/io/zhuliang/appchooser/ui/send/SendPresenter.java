package io.zhuliang.appchooser.ui.send;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import androidx.annotation.NonNull;

import java.util.List;

import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.data.ResolveInfosRepository;
import io.zhuliang.appchooser.ui.resolveinfos.ResolveInfosPresenter;
import io.zhuliang.appchooser.util.MimeType;
import io.zhuliang.appchooser.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author Zhu Liang
 */

class SendPresenter extends ResolveInfosPresenter<SendContract.View>
        implements SendContract.Presenter {

    SendPresenter(@NonNull SendContract.View view,
                  @NonNull BaseSchedulerProvider schedulerProvider,
                  @NonNull ActionConfig actionConfig,
                  @NonNull ResolveInfosRepository resolveInfosRepository) {
        super(view, schedulerProvider, actionConfig, resolveInfosRepository);

        if (!Intent.ACTION_SEND.equals(actionConfig.actionName)) {
            throw new IllegalArgumentException(actionConfig.actionName +
                    " is not " + Intent.ACTION_SEND);
        }
        if (actionConfig.text == null) {
            throw new NullPointerException("actionConfig.text == null");
        }
        if (!MimeType.TEXT_PLAIN.equals(actionConfig.mimeType)) {
            throw new IllegalArgumentException(actionConfig.mimeType + " is not " +
                    MimeType.TEXT_PLAIN);
        }
    }

    @Override
    public void subscribe() {
        loadResolveInfos();
    }

    /**
     * @see #subscribe() 会调用此方法
     */
    @Override
    public void loadResolveInfos() {

        mView.setLoadingIndicator(true);

        Intent intent = new Intent(mActionConfig.actionName);
        intent.putExtra(Intent.EXTRA_TEXT, mActionConfig.text);
        intent.setType(mActionConfig.mimeType);
        mSubscriptions.clear();
        Subscription subscription = mResolveInfosRepository
                .listIntentActivities(intent)
                .flatMap(new Func1<List<ResolveInfo>, Observable<ResolveInfo>>() {
                    @Override
                    public Observable<ResolveInfo> call(List<ResolveInfo> resolveInfos) {
                        return Observable.from(resolveInfos);
                    }
                })
                .filter(new Func1<ResolveInfo, Boolean>() {
                    @Override
                    public Boolean call(ResolveInfo resolveInfo) {
                        return !containsInExcluded(resolveInfo);
                    }
                })
                .toList()
                .subscribe(
                        new Action1<List<ResolveInfo>>() {
                            @Override
                            public void call(List<ResolveInfo> resolveInfos) {
                                if (resolveInfos == null || resolveInfos.isEmpty()) {
                                    mView.showNoResolveInfos();
                                    mView.dismissDialog();
                                } else if (resolveInfos.size() == 1) {
                                    openResolveInfo(resolveInfos.get(0));
                                } else {
                                    mView.setLoadingIndicator(false);
                                    mView.showResolveInfos(resolveInfos);
                                }
                            }
                        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void openResolveInfo(@NonNull ResolveInfo resolveInfo) {
        Intent intent = new Intent(mActionConfig.actionName);
        intent.putExtra(Intent.EXTRA_TEXT, mActionConfig.text);
        intent.setType(mActionConfig.mimeType);
        intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name));
        try {
            mView.showActivity(intent, mActionConfig.fromActivity);
        } catch (ActivityNotFoundException e) {
            mView.showNoResolveInfos();
        }
    }
}
