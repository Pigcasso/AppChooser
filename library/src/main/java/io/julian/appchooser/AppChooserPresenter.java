package io.julian.appchooser;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.ActivityInfosRepository;
import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.data.ResolversRepository;
import io.julian.appchooser.exception.AppChooserException;
import io.julian.appchooser.util.schedulers.BaseSchedulerProvider;
import io.julian.common.Preconditions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/25 上午11:12
 */
class AppChooserPresenter implements AppChooserContract.Presenter {

    private AppChooserContract.View mView;
    private File mFile;
    private String mRealMimeType;
    private List<ComponentName> mExcluded;
    private ActivityInfosRepository mActivityInfosRepository;
    private ResolversRepository mResolversRepository;
    private BaseSchedulerProvider mSchedulerProvider;
    private CompositeSubscription mSubscription = new CompositeSubscription();

    AppChooserPresenter(AppChooserContract.View view,
                        BaseSchedulerProvider schedulerProvider,
                        File file,
                        String mimeType,
                        ActivityInfosRepository activityInfosRepository,
                        ResolversRepository resolversRepository) {
        this(view, schedulerProvider, file, mimeType, null, activityInfosRepository,
                resolversRepository);
    }

    AppChooserPresenter(AppChooserContract.View view,
                        BaseSchedulerProvider schedulerProvider,
                        File file,
                        String mimeType,
                        List<ComponentName> excluded,
                        ActivityInfosRepository activityInfosRepository,
                        ResolversRepository resolversRepository) {
        mView = view;
        mSchedulerProvider = schedulerProvider;
        mFile = file;
        mActivityInfosRepository = activityInfosRepository;
        mResolversRepository = resolversRepository;
        mRealMimeType = mimeType == null ? AppChooserContract.DEFAULT_MIME_TYPE : mimeType;
        mExcluded = excluded;
    }

    AppChooserPresenter(BaseSchedulerProvider schedulerProvider,
                        ActivityInfosRepository activityInfosRepository) {
        mSchedulerProvider = schedulerProvider;
        mActivityInfosRepository = activityInfosRepository;
    }

    @Override
    public void subscribe() {
        loadActivityInfo();
    }

    @Override
    public void unsubscribe() {
        mSubscription.clear();
    }

    @Override
    public void loadActivityInfo(final String mimeType) {
        Subscription subscription = mActivityInfosRepository
                .getActivityInfo(mimeType)
                .map(new Func1<ActivityInfo, ActivityInfo>() {
                    @Override
                    public ActivityInfo call(ActivityInfo activityInfo) {
                        if (activityInfo != null && containsInExcluded(activityInfo)) {
                            mActivityInfosRepository.deleteActivityInfo(mimeType);
                            return null;
                        }
                        return activityInfo;
                    }
                })
                .subscribe(new Action1<ActivityInfo>() {
                    @Override
                    public void call(ActivityInfo activityInfo) {
                        if (activityInfo != null) {
                            try {
                                mView.showFileContent(activityInfo, mFile);
                                return;
                            } catch (AppChooserException e) {
                                mActivityInfosRepository.deleteActivityInfo(mimeType);
                            }
                        }
                        if (mimeType.equals(AppChooserContract.DEFAULT_MIME_TYPE)) {
                            mView.showMediaTypes();
                        } else {
                            loadResolvers(mimeType);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void loadResolvers(final String mimeType) {
        Subscription subscription = loadResolversInternal(mimeType)
                .subscribe(new Action1<List<Resolver>>() {
                    @Override
                    public void call(List<Resolver> resolvers) {
                        if (resolvers == null || resolvers.size() == 0) {
                            mView.showMediaTypes();
                        } else if (resolvers.size() == 1) {
                            try {
                                mView.showFileContent(resolvers.get(0).loadActivityInfo(mimeType), mFile);
                            } catch (AppChooserException e) {
                                mView.showMediaTypes();
                            }
                        } else {
                            mView.showResolvers(resolvers);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void loadResolvers(final MediaType mediaType) {
        Subscription subscription = loadResolversInternal(mediaType.getMimeType())
                .subscribe(new Action1<List<Resolver>>() {
                    @Override
                    public void call(List<Resolver> resolvers) {
                        if (resolvers == null || resolvers.size() == 0) {
                            mView.showNoResolvers(mediaType);
                        } else if (resolvers.size() == 1) {
                            try {
                                mView.showFileContent(resolvers.get(0).loadActivityInfo(mediaType.getMimeType()), mFile);
                            } catch (AppChooserException e) {
                                mView.showMediaTypes();
                            }
                        } else {
                            mView.showResolvers(resolvers);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void loadResolver(Resolver resolver) {
        Subscription subscription = Observable
                .just(resolver)
                .map(new Func1<Resolver, ActivityInfo>() {
                    @Override
                    public ActivityInfo call(Resolver resolver) {
                        ActivityInfo activityInfo = resolver.loadActivityInfo(mRealMimeType);
                        if (resolver.isDefault()) {
                            mActivityInfosRepository.saveActivityInfo(activityInfo);
                        }
                        return activityInfo;
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<ActivityInfo>() {
                    @Override
                    public void call(ActivityInfo activityInfo) {
                        try {
                            mView.showFileContent(activityInfo, mFile);
                        } catch (AppChooserException e) {
                            mView.showFileContentError(mFile);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void cleanAllActivityInfos() {
        Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(Subscriber<? super Void> subscriber) {
                        mActivityInfosRepository.deleteAllActivityInfos();
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .subscribe();
    }

    private void loadActivityInfo() {
        loadActivityInfo(mRealMimeType);
    }

    private Observable<List<Resolver>> loadResolversInternal(final String mimeType) {
        return Observable
                .just((Void) null)
                .flatMap(new Func1<Void, Observable<List<Resolver>>>() {
                    @Override
                    public Observable<List<Resolver>> call(Void aVoid) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(mFile), mimeType);
                        return mResolversRepository.listResolvers(intent);
                    }
                })
                .flatMap(new Func1<List<Resolver>, Observable<Resolver>>() {
                    @Override
                    public Observable<Resolver> call(List<Resolver> resolvers) {
                        return Observable.from(resolvers);
                    }
                })
                .filter(new Func1<Resolver, Boolean>() { // 过滤掉包含在 excluded 集合中的 resolver
                    @Override
                    public Boolean call(Resolver resolver) {
                        return !containsInExcluded(resolver);
                    }
                })
                .toList();
    }

    /**
     * 当前这个 resolver 是否包含在 {@link #mExcluded} 中
     *
     * @return 如果包含在 {@link #mExcluded} 中，则返回 true；否则返回 false。
     */
    private Boolean containsInExcluded(@NonNull Resolver resolver) {
        Preconditions.checkNotNull(resolver, "resolver == null");
        if (mExcluded == null || mExcluded.size() == 0) {
            return false;
        }
        ComponentName componentName = resolver.loadComponentName();
        for (ComponentName excluded : mExcluded) {
            if (excluded.equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前这个 activityInfo 是否包含在 {@link #mExcluded} 中
     *
     * @return 如果包含在 {@link #mExcluded} 中，则返回 true；否则返回 false。
     */
    private Boolean containsInExcluded(@NonNull ActivityInfo activityInfo) {
        Preconditions.checkNotNull(activityInfo, "activityInfo == null");
        if (mExcluded == null || mExcluded.size() == 0) {
            return false;
        }
        ComponentName componentName = new ComponentName(activityInfo.getPkg(),
                activityInfo.getCls());
        for (ComponentName excluded : mExcluded) {
            if (excluded.equals(componentName)) {
                return true;
            }
        }
        return false;
    }
}
