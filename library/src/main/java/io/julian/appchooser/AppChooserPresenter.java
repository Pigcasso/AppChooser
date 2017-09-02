package io.julian.appchooser;

import io.julian.appchooser.data.ActivityInfosRepository;
import io.julian.mvp.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.Subscriber;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/25 上午11:12
 */
class AppChooserPresenter implements AppChooserContract.Presenter {

    private ActivityInfosRepository mActivityInfosRepository;
    private BaseSchedulerProvider mSchedulerProvider;

    AppChooserPresenter(BaseSchedulerProvider schedulerProvider,
                        ActivityInfosRepository activityInfosRepository) {
        mSchedulerProvider = schedulerProvider;
        mActivityInfosRepository = activityInfosRepository;
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
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
}
