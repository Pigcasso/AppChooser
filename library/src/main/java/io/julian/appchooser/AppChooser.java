package io.julian.appchooser;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.lang.ref.WeakReference;

import io.julian.appchooser.action.SendAction;
import io.julian.appchooser.action.ViewAction;

/**
 * @author Zhu Liang
 */

public final class AppChooser {

    private WeakReference<FragmentActivity> mActivity;
    private WeakReference<Fragment> mFragment;

    private AppChooser(FragmentActivity activity) {
        this(activity, null);
    }

    private AppChooser(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private AppChooser(FragmentActivity activity, Fragment fragment) {
        mActivity = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static AppChooser from(FragmentActivity activity) {
        return new AppChooser(activity);
    }

    public static AppChooser from(Fragment fragment) {
        return new AppChooser(fragment);
    }

    public SendAction text(String text) {
        return new SendAction(this).text(text);
    }

    public ViewAction file(File file) {
        return new ViewAction(this).file(file);
    }

    @Nullable
    public FragmentActivity getActivity() {
        return mActivity.get();
    }

    @Nullable
    public Fragment getFragment() {
        return mFragment == null ? null : mFragment.get();
    }

    public void cleanDefaults() {
        if (mActivity != null) {
            Injection.provideActivityInfosRepository(mActivity.get()).deleteAllActivityInfos();
        }
    }
}
