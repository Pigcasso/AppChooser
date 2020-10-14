package io.zhuliang.appchooser.sample.module.mediastoreimages;

import android.app.Application;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.zhuliang.appchooser.sample.SampleInjection;
import io.zhuliang.appchooser.sample.data.MediaStoreImage;
import io.zhuliang.appchooser.sample.data.MediaStoreImagesRepository;
import io.zhuliang.appchooser.sample.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MediaStoreImagesViewModel extends AndroidViewModel {

    private MediaStoreImagesRepository mRepository;
    private CompositeSubscription mSubscriptions;
    private BaseSchedulerProvider mSchedulerProvider;

    private ContentObserver mContentObserver;

    private MutableLiveData<List<MediaStoreImage>> mMediaStoreImages = new MutableLiveData<>();
    public LiveData<List<MediaStoreImage>> mediaStoreImages = mMediaStoreImages;

    public MediaStoreImagesViewModel(Application application) {
        super(application);
        mRepository = SampleInjection.provideMediaStoreImagesRepository(application.getApplicationContext());
        mSubscriptions = new CompositeSubscription();
        mSchedulerProvider = SampleInjection.provideSchedulerProvider();
    }

    @Override
    protected void onCleared() {
        mSubscriptions.clear();
        if (mContentObserver != null) {
            getApplication().getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onCleared();
    }

    public void loadImages() {
        Subscription subscription = Observable
                .create(new Observable.OnSubscribe<List<MediaStoreImage>>() {
                    @Override
                    public void call(Subscriber<? super List<MediaStoreImage>> subscriber) {
                        List<MediaStoreImage> mediaStoreImages = mRepository.listImages();
                        subscriber.onNext(mediaStoreImages);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Action1<List<MediaStoreImage>>() {
                    @Override
                    public void call(List<MediaStoreImage> mediaStoreImages) {
                        mMediaStoreImages.postValue(mediaStoreImages);
                    }
                });
        mSubscriptions.add(subscription);

        if (mContentObserver == null) {
            ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                @Override
                public void onChange(boolean selfChange) {
                    loadImages();
                }
            };
            getApplication().getContentResolver().registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver
            );
            mContentObserver = contentObserver;
        }
    }
}
