package io.julian.appchooser.data.local;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

import io.julian.appchooser.data.ActivityInfosDataSource;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/5 下午4:08
 */
@RunWith(AndroidJUnit4.class)
public class ActivityInfosSharedPreferencesDataSourceTest extends BaseActivityInfosDataSourceTest {

    @NonNull
    @Override
    ActivityInfosDataSource getDataSource() {
        return new ActivityInfosSharedPreferencesDataSource(
                InstrumentationRegistry.getContext().getApplicationContext());
    }
}