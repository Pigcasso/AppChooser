package io.zhuliang.appchooser.ui.view;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;

import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.data.ActivityInfo;
import io.zhuliang.appchooser.data.ActivityInfosRepository;
import io.zhuliang.appchooser.data.MediaType;
import io.zhuliang.appchooser.data.MediaTypesRepository;
import io.zhuliang.appchooser.data.ResolveInfosRepository;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.ui.resolveinfos.ResolveInfosPresenter;
import io.zhuliang.appchooser.util.FileUtils;
import io.zhuliang.appchooser.util.MimeType;
import io.zhuliang.appchooser.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author Zhu Liang
 */

class ViewPresenter extends ResolveInfosPresenter<ViewContract.View> implements ViewContract.Presenter {

    @NonNull
    private ActivityInfosRepository mActivityInfosRepository;
    @NonNull
    private MediaTypesRepository mMediaTypesRepository;

    ViewPresenter(@NonNull ViewContract.View view,
                  @NonNull BaseSchedulerProvider schedulerProvider,
                  @NonNull ActionConfig actionConfig,
                  @NonNull ResolveInfosRepository resolveInfosRepository,
                  @NonNull ActivityInfosRepository activityInfosRepository,
                  @NonNull MediaTypesRepository mediaTypesRepository) {
        super(view, schedulerProvider, actionConfig, resolveInfosRepository);

        if (!Intent.ACTION_VIEW.equals(actionConfig.actionName)) {
            throw new IllegalArgumentException(actionConfig.actionName + " is not " +
                    Intent.ACTION_VIEW);
        }
        if (actionConfig.pathname == null) {
            throw new NullPointerException("actionConfig.pathname == null");
        }
        if (activityInfosRepository == null) {
            throw new NullPointerException("activityInfoRepository == null");
        }
        if (mediaTypesRepository == null) {
            throw new NullPointerException("mediaTypesRepository == null");
        }
        mActivityInfosRepository = activityInfosRepository;
        mMediaTypesRepository = mediaTypesRepository;

        File file = new File(actionConfig.pathname);
        FileUtils.checkFile(file);
        String mimeType = MimeType.getMimeType(file);
        if (mimeType == null) {
            mimeType = MimeType.ALL;
        }
        actionConfig.mimeType = mimeType;
    }

    @Override
    public void subscribe() {
        loadActivityInfo();
    }

    @Override
    public void loadActivityInfo() {
        mSubscriptions.clear();
        Subscription subscription = mActivityInfosRepository
                .getActivityInfo(mActionConfig.mimeType)
                .map(new Func1<ActivityInfo, ActivityInfo>() {
                    @Override
                    public ActivityInfo call(ActivityInfo activityInfo) {
                        if (containsInExcluded(activityInfo)) {
                            return null;
                        }
                        return activityInfo;
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<ActivityInfo>() {
                    @Override
                    public void call(ActivityInfo activityInfo) {
                        if (activityInfo == null) {
                            loadMediaTypesOrResolveInfos();
                        } else {
                            try {
                                showActivityInternal(activityInfo);
                            } catch (ActivityNotFoundException e) {
                                loadMediaTypesOrResolveInfos();
                            }
                        }
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void loadMediaTypes() {

        mView.setLoadingIndicator(true);

        mSubscriptions.clear();
        Subscription subscription = mMediaTypesRepository
                .listMediaTypes()
                .subscribe(new Subscriber<List<MediaType>>() {
                    @Override
                    public void onCompleted() {
                        mView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<MediaType> mediaTypes) {
                        mView.showMediaTypes(mediaTypes);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void openMediaType(@NonNull MediaType mediaType) {
        mActionConfig.mimeType = mediaType.getMimeType();
        loadResolveInfos();
    }

    @Override
    public void loadResolveInfos() {

        mView.setLoadingIndicator(true);

        Intent intent = new Intent(mActionConfig.actionName);
        intent.setDataAndType(Uri.fromFile(new File(mActionConfig.pathname)),
                mActionConfig.mimeType);
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
        final boolean isDefault = mView.isAsDefault();
        Subscription subscription = Observable
                .just(resolveInfo)
                .map(new Func1<ResolveInfo, ActivityInfo>() {
                    @Override
                    public ActivityInfo call(ResolveInfo resolveInfo) {
                        return new ActivityInfo(mActionConfig.mimeType,
                                resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    }
                })
                .doOnNext(new Action1<ActivityInfo>() {
                    @Override
                    public void call(ActivityInfo activityInfo) {
                        if (isDefault) {
                            mActivityInfosRepository.saveActivityInfo(activityInfo);
                        }
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<ActivityInfo>() {
                    @Override
                    public void call(ActivityInfo activityInfo) {
                        showActivityInternal(activityInfo);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void showActivityInternal(ActivityInfo activityInfo) {
        if (mActionConfig.requestCode == -1) {
            mView.showActivity(makeIntent(activityInfo), mActionConfig.fromActivity);
        } else {
            mView.showActivity(makeIntent(activityInfo), mActionConfig.requestCode,
                    mActionConfig.fromActivity);
        }
        mView.dismissDialog();
    }

    private Intent makeIntent(@NonNull ActivityInfo activityInfo) {
        Preconditions.checkNotNull(activityInfo);
        Intent intent = new Intent(mActionConfig.actionName);
        intent.setComponent(new ComponentName(activityInfo.getPkg(), activityInfo.getCls()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(mView.getContext(), mActionConfig.authority, new File(mActionConfig.pathname));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, mActionConfig.mimeType);
        } else {
            Uri uri = Uri.fromFile(new File(mActionConfig.pathname));
            intent.setDataAndType(uri, mActionConfig.mimeType);
        }
        return intent;
    }

    private void loadMediaTypesOrResolveInfos() {
        if (MimeType.ALL.equals(mActionConfig.mimeType)) {
            loadMediaTypes();
        } else {
            loadResolveInfos();
        }
    }
}
