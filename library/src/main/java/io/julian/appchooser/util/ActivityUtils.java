package io.julian.appchooser.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.NonNull;

import static io.julian.common.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/14 上午9:44
 */

public class ActivityUtils {

    public static void remove(@NonNull FragmentManager fm, @NonNull String tag) {
        checkNotNull(fm);
        checkNotNull(tag);

        Fragment prev = fm.findFragmentByTag(tag);
        if (prev != null) {
            fm.beginTransaction().remove(prev).commit();
        }
    }

    public static void remove(@NonNull android.support.v4.app.FragmentManager fm, @NonNull String tag) {
        checkNotNull(fm);
        checkNotNull(tag);

        android.support.v4.app.Fragment prev = fm.findFragmentByTag(tag);
        if (prev != null) {
            fm.beginTransaction().remove(prev).commit();
        }
    }
}
