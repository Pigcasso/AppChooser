package io.julian.appchooser;

import android.content.Context;
import android.support.annotation.NonNull;

import io.julian.appchooser.data.ActivityInfosRepository;
import io.julian.appchooser.data.MediaTypesRepository;
import io.julian.appchooser.data.ResolversRepository;
import io.julian.appchooser.data.local.ActivityInfosSharedPreferencesDataSource;
import io.julian.appchooser.data.local.MediaTypesLocalDataSource;
import io.julian.mvp.util.schedulers.BaseSchedulerProvider;
import io.julian.mvp.util.schedulers.SchedulerProvider;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午2:27
 */

public class Injection {

    public static MediaTypesRepository provideMediaTypesRepository(@NonNull Context context) {
        return new MediaTypesRepository(new MediaTypesLocalDataSource(context));
    }

    public static ActivityInfosRepository provideActivityInfosRepository(@NonNull Context context) {
        return new ActivityInfosRepository(new ActivityInfosSharedPreferencesDataSource(context));
    }

    public static ResolversRepository providerResolversRepository(@NonNull Context context) {
        return new ResolversRepository(context);
    }

    public static BaseSchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }
}
