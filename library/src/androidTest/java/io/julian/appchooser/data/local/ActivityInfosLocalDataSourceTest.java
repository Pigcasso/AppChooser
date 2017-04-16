package io.julian.appchooser.data.local;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

import io.julian.appchooser.data.ActivityInfosDataSource;
import io.julian.appchooser.util.schedulers.ImmediateSchedulerProvider;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午3:34
 */
@RunWith(AndroidJUnit4.class)
public class ActivityInfosLocalDataSourceTest extends BaseActivityInfosDataSourceTest {

    @NonNull
    @Override
    ActivityInfosDataSource getDataSource() {
        return ActivityInfosLocalDataSource.getInstance(InstrumentationRegistry.getContext(),
                new ImmediateSchedulerProvider());
    }
}