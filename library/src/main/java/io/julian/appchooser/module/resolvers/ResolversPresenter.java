package io.julian.appchooser.module.resolvers;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.List;

import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.ActivityInfosRepository;
import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.data.MediaTypesRepository;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.data.ResolversRepository;
import io.julian.appchooser.exception.AppChooserException;
import io.julian.common.Preconditions;
import io.julian.mvp.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Zhu Liang
 */

public class ResolversPresenter implements ResolversContract.Presenter {

    private final ResolversContract.View mView;
    private final BaseSchedulerProvider mSchedulerProvider;
    private File mFile;
    private String mMimeType;
    private int mRequestCode;
    private List<ComponentName> mExcluded;
    private ActivityInfosRepository mActivityInfosRepository;
    private final ResolversRepository mResolversRepository;
    private final MediaTypesRepository mMediaTypesRepository;
    private CompositeSubscription mSubscription;

    public ResolversPresenter(@NonNull ResolversContract.View view,
                              @NonNull BaseSchedulerProvider schedulerProvider,
                              @NonNull File file,
                              String mimeType,
                              int requestCode,
                              @Nullable List<ComponentName> excluded,
                              @NonNull ActivityInfosRepository activityInfosRepository,
                              @NonNull MediaTypesRepository mediaTypesRepository,
                              @NonNull ResolversRepository resolversRepository) {
        Preconditions.checkNotNull(file, "file == null");
        Preconditions.checkArgument(file.exists(), file.getName() + " had not exist.");
        Preconditions.checkArgument(file.isFile(), file.getName() + " is not a file.");

        mView = Preconditions.checkNotNull(view, "view == null");
        mSchedulerProvider = Preconditions.checkNotNull(schedulerProvider,
                "schedulerProvider == null");
        mFile = file;
        mMimeType = mimeType == null ? ResolversConsts.DEFAULT_MIME_TYPE : mimeType;

        mRequestCode = requestCode;
        mExcluded = excluded;
        mActivityInfosRepository = Preconditions.checkNotNull(activityInfosRepository,
                "activityInfosRepository == null");
        mMediaTypesRepository = Preconditions.checkNotNull(mediaTypesRepository,
                "mediaTypesRepository == null");
        mResolversRepository = Preconditions.checkNotNull(resolversRepository,
                "resolversRepository == null");
        mSubscription = new CompositeSubscription();

        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadActivityInfo(mMimeType);
    }

    @Override
    public void unsubscribe() {
        mSubscription.clear();
    }

    @Override
    public void loadActivityInfo(final @NonNull String mimeType) {

        mSubscription.clear();

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
                                mView.showFileContent(activityInfo, mFile, mRequestCode);
                                mView.dismissDialog();
                                return;
                            } catch (AppChooserException e) {
                                mActivityInfosRepository.deleteActivityInfo(mimeType);
                            }
                        }
                        if (mimeType.equals(ResolversConsts.DEFAULT_MIME_TYPE)) {
                            loadMediaTypes();
                        } else {
                            loadResolvers(mimeType);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void loadMediaTypes() {
        mSubscription.clear();

        Subscription subscription = mMediaTypesRepository
                .listMediaTypes()
                .observeOn(mSchedulerProvider.ui())
                .subscribeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<List<MediaType>>() {
                    @Override
                    public void call(List<MediaType> mediaTypes) {
                        mView.showMediaTypes(mediaTypes);
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void loadResolvers(@NonNull final MediaType mediaType) {
        mSubscription.clear();

        Subscription subscription = loadResolversInternal(mediaType.getMimeType())
                .subscribe(new Action1<List<Resolver>>() {
                    @Override
                    public void call(List<Resolver> resolvers) {
                        if (resolvers == null || resolvers.size() == 0) {
                            mView.showNoResolvers(mediaType);
                        } else if (resolvers.size() == 1) {
                            try {
                                mView.showFileContent(resolvers.get(0).loadActivityInfo(mediaType.getMimeType()), mFile, mRequestCode);
                            } catch (AppChooserException e) {
                                loadMediaTypes();
                            }
                        } else {
                            mView.showResolvers(resolvers);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void loadResolvers(@NonNull final String mimeType) {
        mSubscription.clear();

        Subscription subscription = loadResolversInternal(mimeType)
                .subscribe(new Action1<List<Resolver>>() {
                    @Override
                    public void call(List<Resolver> resolvers) {
                        if (resolvers == null || resolvers.size() == 0) {
                            loadMediaTypes();
                        } else if (resolvers.size() == 1) {
                            try {
                                mView.showFileContent(resolvers.get(0).loadActivityInfo(mimeType), mFile, mRequestCode);
                            } catch (AppChooserException e) {
                                loadMediaTypes();
                            }
                        } else {
                            mView.showResolvers(resolvers);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void loadResolver(@NonNull Resolver resolver) {
        Subscription subscription = Observable
                .just(resolver)
                .map(new Func1<Resolver, ActivityInfo>() {
                    @Override
                    public ActivityInfo call(Resolver resolver) {
                        ActivityInfo activityInfo = resolver.loadActivityInfo(mMimeType);
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
                            mView.showFileContent(activityInfo, mFile, mRequestCode);
                        } catch (AppChooserException e) {
                            mView.showFileContentError(mFile);
                        } finally {
                            mView.dismissDialog();
                        }
                    }
                });
        mSubscription.add(subscription);
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
}
