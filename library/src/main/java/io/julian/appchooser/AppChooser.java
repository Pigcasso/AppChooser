package io.julian.appchooser;

import android.app.Activity;
import android.content.ComponentName;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import io.julian.appchooser.module.resolvers.ResolversFragment;
import io.julian.common.Preconditions;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/14 上午9:36
 */

public class AppChooser implements AppChooserContract.View {

    private static final String TAG_DISPLAYINGS = "tag_resolvers";

    private Activity mActivity;
    private File mFile;
    private int mRequestCode;
    private ArrayList<ComponentName> mExcluded;
    private AppChooserContract.Presenter mPresenter;

    private AppChooser(@NonNull Activity activity) {
        mActivity = Preconditions.checkNotNull(activity, "activity == null");
        mPresenter = new AppChooserPresenter(Injection.provideSchedulerProvider(),
                Injection.provideActivityInfosRepository(mActivity));
    }

    public static AppChooser with(Activity activity) {
        return new AppChooser(activity);
    }

    public AppChooser file(File file) {
        Preconditions.checkNotNull(file, "file == null");
        Preconditions.checkArgument(file.exists(), file.getName() + " does not exist");
        Preconditions.checkArgument(file.isFile(), file.getName() + " is not file");

        mFile = file;
        return this;
    }

    public AppChooser requestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    public AppChooser excluded(ComponentName... componentNames) {
        if (componentNames != null) {
            mExcluded = new ArrayList<>(Arrays.asList(componentNames));
        }
        return this;
    }

    public void load() {
        showDisplayings();
    }

    public void cleanDefaults() {
        if (mPresenter != null) {
            mPresenter.cleanAllActivityInfos();
        }
    }

    public void bind() {
        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    public void unbind() {
        if (mPresenter != null) {
            mPresenter.unsubscribe();
        }
    }

    @Override
    public void showDisplayings() {
        if (mActivity instanceof AppCompatActivity) {
            io.julian.appchooser.module.resolvers.v4.ResolversFragment
                    .newInstance(mFile, mRequestCode, mExcluded)
                    .show(((AppCompatActivity) mActivity).getSupportFragmentManager(),
                            TAG_DISPLAYINGS);
        } else {
            ResolversFragment.newInstance(mFile, mRequestCode, mExcluded)
                    .show(mActivity.getFragmentManager(), TAG_DISPLAYINGS);
        }
    }

    @Override
    public void setPresenter(@NonNull AppChooserContract.Presenter presenter) {

    }
}
