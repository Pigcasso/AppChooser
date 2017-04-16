package io.julian.appchooser.util;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;

import static io.julian.appchooser.util.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/14 上午9:44
 */

public class ActivityUtils {

    public static void showDialog(@NonNull FragmentManager fm,
                                  @NonNull DialogFragment dialogFragment,
                                  @NonNull String tag) {
        checkNotNull(fm);
        checkNotNull(dialogFragment);
        checkNotNull(tag);

        dialogFragment.show(fm, tag);
    }

    public static void showDialog(@NonNull FragmentTransaction ft,
                                  @NonNull DialogFragment dialogFragment,
                                  @NonNull String tag) {
        checkNotNull(ft);
        checkNotNull(dialogFragment);
        checkNotNull(tag);

        dialogFragment.show(ft, tag);
    }

    public static void showDialog(@NonNull android.support.v4.app.FragmentManager fm,
                                  @NonNull android.support.v4.app.DialogFragment dialogFragment,
                                  @NonNull String tag) {
        checkNotNull(fm);
        checkNotNull(dialogFragment);
        checkNotNull(tag);

        dialogFragment.show(fm, tag);
    }

    public static void showDialog(@NonNull android.support.v4.app.FragmentTransaction ft,
                                  @NonNull android.support.v4.app.DialogFragment dialogFragment,
                                  @NonNull String tag) {
        checkNotNull(ft);
        checkNotNull(dialogFragment);
        checkNotNull(tag);

        dialogFragment.show(ft, tag);
    }

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
