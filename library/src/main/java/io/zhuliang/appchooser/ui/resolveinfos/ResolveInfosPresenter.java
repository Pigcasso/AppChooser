package io.zhuliang.appchooser.ui.resolveinfos;

import android.content.ComponentName;
import android.content.pm.ResolveInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.data.ActivityInfo;
import io.zhuliang.appchooser.data.ResolveInfosRepository;
import io.zhuliang.appchooser.util.schedulers.BaseSchedulerProvider;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Zhu Liang
 */

public abstract class ResolveInfosPresenter<V extends ResolveInfosContract.View> implements ResolveInfosContract.Presenter {

    @NonNull
    protected V mView;
    @NonNull
    protected BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    protected ActionConfig mActionConfig;
    @NonNull
    protected CompositeSubscription mSubscriptions;
    @NonNull
    protected ResolveInfosRepository mResolveInfosRepository;

    public ResolveInfosPresenter(@NonNull V view,
                                 @NonNull BaseSchedulerProvider schedulerProvider,
                                 @NonNull ActionConfig actionConfig,
                                 @NonNull ResolveInfosRepository resolveInfosRepository) {
        if (view == null) {
            throw new NullPointerException("view == null");
        }
        if (schedulerProvider == null) {
            throw new NullPointerException("schedulerProvider == null");
        }
        if (actionConfig == null) {
            throw new NullPointerException("actionConfig == null");
        }

        if (resolveInfosRepository == null) {
            throw new NullPointerException("resolveInfosRepository == null");
        }
        mView = view;
        mSubscriptions = new CompositeSubscription();
        mSchedulerProvider = schedulerProvider;
        mActionConfig = actionConfig;
        mResolveInfosRepository = resolveInfosRepository;

        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    protected boolean containsInExcluded(@Nullable ActivityInfo activityInfo) {
        if (activityInfo == null) {
            return false;
        }
        if (mActionConfig.excluded == null) {
            return false;
        }
        for (ComponentName componentName : mActionConfig.excluded) {
            if (componentName.getPackageName().equals(activityInfo.getPkg())
                    && componentName.getClassName().equals(activityInfo.getCls())) {
                return true;
            }
        }
        return false;
    }

    protected boolean containsInExcluded(@Nullable ResolveInfo resolveInfo) {
        if (resolveInfo == null) {
            return false;
        }
        if (mActionConfig.excluded == null) {
            return false;
        }
        if (resolveInfo.activityInfo == null) {
            return false;
        }
        String cls = resolveInfo.activityInfo.name;
        String pkg = resolveInfo.activityInfo.packageName;
        for (ComponentName componentName : mActionConfig.excluded) {
            if (componentName.getPackageName().equals(pkg)
                    && componentName.getClassName().equals(cls)) {
                return true;
            }
        }
        return false;
    }
}
