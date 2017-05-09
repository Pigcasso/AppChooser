package io.julian.appchooser;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import io.julian.appchooser.data.ActivityInfo;
import io.julian.appchooser.data.MediaType;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.exception.AppChooserException;
import io.julian.appchooser.module.mediatypes.MediaTypesDialogFragment;
import io.julian.appchooser.module.resolvers.ResolversDialogFragment;
import io.julian.appchooser.util.ActivityUtils;
import io.julian.appchooser.util.MimeTypeUtils;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/14 上午9:36
 */

public class AppChooser implements AppChooserContract.View {

    private static final String TAG_RESOLVES = "tag_resolvers";
    private static final String TAG_MIME_TYPES = "tag_mime_types";

    private Activity mActivity;
    private File mFile;
    private int mRequestCode;
    private AppChooserContract.Presenter mPresenter;
    private DialogCompatImpl mDialogCompat;

    private AppChooser(Activity activity) {
        mActivity = activity;
        if (mActivity instanceof FragmentActivity) {
            mDialogCompat = new SupportDialogCompatImpl((FragmentActivity) mActivity);
        } else {
            mDialogCompat = new HCDialogCompatImpl(mActivity);
        }
    }

    public static AppChooser with(Activity activity) {
        return new AppChooser(activity);
    }

    public AppChooser file(File file) {
        mFile = file;
        return this;
    }

    public AppChooser requestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    public void load() {
        mPresenter = new AppChooserPresenter(this,
                Injection.provideSchedulerProvider(),
                mFile,
                MimeTypeUtils.getMimeType(mFile),
                Injection.provideActivityInfosRepository(mActivity),
                Injection.providerResolversRepository(mActivity));
        mPresenter.subscribe();
    }

    @Override
    public void showFileContent(ActivityInfo activityInfo, File file) throws AppChooserException {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(activityInfo.getPkg(), activityInfo.getCls()));
        intent.setData(Uri.fromFile(file));
        ComponentName componentName = intent.resolveActivity(mActivity.getPackageManager());
        if (componentName != null) {
            if (mRequestCode == AppChooserContract.DEFAULT_REQUEST_CODE) {
                mActivity.startActivity(intent);
            } else {
                mActivity.startActivityForResult(intent, mRequestCode);
            }
        } else {
            throw new AppChooserException();
        }
    }

    @Override
    public void showMediaTypes() {
        mDialogCompat.showMediaTypes();
    }

    @Override
    public void showResolvers(List<Resolver> resolvers) {
        mDialogCompat.showResolvers(resolvers);
    }

    @Override
    public void hideResolvers() {
        mDialogCompat.hideResolvers();
    }

    @Override
    public void showFileContentError(File file) {
        Toast.makeText(mActivity, "打开" + file + "失败！！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNoResolvers(MediaType mediaType) {
        Toast.makeText(mActivity, "没有找到能打开" + mediaType.getDisplayName() + "的应用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(@NonNull AppChooserContract.Presenter presenter) {
        throw new UnsupportedOperationException();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMediaType(MediaType mediaType) {
        mPresenter.loadResolvers(mediaType);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResolver(Resolver resolver) {
        hideResolvers();
        mPresenter.loadResolver(resolver);
    }

    public void cleanDefaults() {
        if (mPresenter == null) {
            mPresenter = new AppChooserPresenter(Injection.provideSchedulerProvider(),
                    Injection.provideActivityInfosRepository(mActivity));
        }
        mPresenter.cleanAllActivityInfos();
    }

    public void bind() {
        EventBus.getDefault().register(this);
    }

    public void unbind() {
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.unsubscribe();
        }
    }

    interface DialogCompatImpl {
        void showMediaTypes();

        void showResolvers(List<Resolver> resolvers);

        void hideResolvers();
    }

    private static class SupportDialogCompatImpl implements DialogCompatImpl {

        private FragmentActivity mActivity;

        private SupportDialogCompatImpl(FragmentActivity activity) {
            mActivity = activity;
        }

        @Override
        public void showMediaTypes() {
            android.support.v4.app.FragmentManager fm = mActivity.getSupportFragmentManager();
            ActivityUtils.remove(fm, TAG_MIME_TYPES);
            ActivityUtils.remove(fm, TAG_RESOLVES);
            io.julian.appchooser.module.mediatypes.v4.MediaTypesDialogFragment.newInstance()
                    .show(fm, TAG_MIME_TYPES);
        }

        @Override
        public void showResolvers(List<Resolver> resolvers) {
            android.support.v4.app.FragmentManager fm = mActivity.getSupportFragmentManager();
            ActivityUtils.remove(fm, TAG_MIME_TYPES);
            ActivityUtils.remove(fm, TAG_RESOLVES);
            io.julian.appchooser.module.resolvers.v4.ResolversDialogFragment.newInstance(resolvers)
                    .show(fm, TAG_RESOLVES);
        }

        @Override
        public void hideResolvers() {
            android.support.v4.app.FragmentManager fm = mActivity.getSupportFragmentManager();
            ActivityUtils.remove(fm, TAG_MIME_TYPES);
            ActivityUtils.remove(fm, TAG_RESOLVES);
        }
    }

    private static class HCDialogCompatImpl implements DialogCompatImpl {

        private Activity mActivity;

        private HCDialogCompatImpl(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void showMediaTypes() {
            FragmentManager fm = mActivity.getFragmentManager();
            ActivityUtils.remove(fm, TAG_MIME_TYPES);
            ActivityUtils.remove(fm, TAG_RESOLVES);
            MediaTypesDialogFragment.newInstance().show(fm, TAG_MIME_TYPES);
        }

        @Override
        public void showResolvers(List<Resolver> resolvers) {
            FragmentManager fm = mActivity.getFragmentManager();
            ActivityUtils.remove(fm, TAG_MIME_TYPES);
            ActivityUtils.remove(fm, TAG_RESOLVES);
            ResolversDialogFragment.newInstance(resolvers).show(fm, TAG_RESOLVES);
        }

        @Override
        public void hideResolvers() {
            FragmentManager fm = mActivity.getFragmentManager();
            ActivityUtils.remove(fm, TAG_MIME_TYPES);
            ActivityUtils.remove(fm, TAG_RESOLVES);
        }
    }
}
