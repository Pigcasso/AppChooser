package io.julian.appchooser.module.mediatypes;

import android.support.annotation.NonNull;

import java.util.List;

import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.data.MediaTypesRepository;
import io.julian.appchooser.util.schedulers.BaseSchedulerProvider;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static io.julian.appchooser.util.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午10:22
 */

public class MediaTypesPresenter implements MediaTypesContract.Presenter {

    @NonNull
    private MediaTypesContract.View mView;
    @NonNull
    private BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private MediaTypesRepository mMediaTypesRepository;
    private CompositeSubscription mSubscription;

    public MediaTypesPresenter(@NonNull MediaTypesContract.View view,
                               @NonNull BaseSchedulerProvider schedulerProvider,
                               @NonNull MediaTypesRepository mediaTypesRepository) {
        mView = checkNotNull(view);
        mSchedulerProvider = checkNotNull(schedulerProvider);
        mMediaTypesRepository = checkNotNull(mediaTypesRepository);
        mSubscription = new CompositeSubscription();

        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadMediaTypes();
    }

    @Override
    public void unsubscribe() {
        mSubscription.unsubscribe();
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
}
