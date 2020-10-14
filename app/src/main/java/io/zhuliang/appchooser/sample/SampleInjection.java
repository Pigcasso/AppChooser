package io.zhuliang.appchooser.sample;


import android.content.Context;

import io.zhuliang.appchooser.Injection;
import io.zhuliang.appchooser.sample.data.FileInfosRepository;
import io.zhuliang.appchooser.sample.data.MediaStoreImagesRepository;
import io.zhuliang.appchooser.sample.util.schedulers.BaseSchedulerProvider;
import io.zhuliang.appchooser.sample.util.schedulers.SchedulerProvider;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午3:53
 */

public class SampleInjection extends Injection {
    public static FileInfosRepository provideFileInfoRepository() {
        return new FileInfosRepository(provideSchedulerProvider());
    }

    public static MediaStoreImagesRepository provideMediaStoreImagesRepository(Context context) {
        return new MediaStoreImagesRepository(context);
    }

    public static BaseSchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }
}
