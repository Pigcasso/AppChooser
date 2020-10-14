package io.zhuliang.appchooser.action;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import io.zhuliang.appchooser.AppChooser;
import io.zhuliang.appchooser.BuildConfig;
import io.zhuliang.appchooser.ui.edit.EditFragment;

/**
 * @author Zhu Liang
 */

public class EditAction {

    private static final String FRAGMENT_TAG = BuildConfig.LIBRARY_PACKAGE_NAME + ".fragment.tag.EDIT";

    private AppChooser mAppChooser;
    private ActionConfig mActionConfig = new ActionConfig();

    public EditAction(AppChooser appChooser) {
        mAppChooser = appChooser;
        mActionConfig.actionName = Intent.ACTION_EDIT;
    }

    public EditAction uri(@NonNull Uri uri) {
        mActionConfig.uri = uri;
        return this;
    }

    public EditAction mimeType(@NonNull String mimeType) {
        mActionConfig.mimeType = mimeType;
        return this;
    }

    public EditAction requestCode(int requestCode) {
        mActionConfig.requestCode = requestCode;
        return this;
    }

    public void load() {
        FragmentActivity activity = mAppChooser.getActivity();
        if (activity != null) {
            mActionConfig.fromActivity = true;
            EditFragment.newInstance(mActionConfig).show(activity.getSupportFragmentManager(),
                    FRAGMENT_TAG);
            return;
        }
        Fragment fragment = mAppChooser.getFragment();
        if (fragment != null) {
            mActionConfig.fromActivity = false;
            EditFragment.newInstance(mActionConfig).show(fragment.getParentFragmentManager(),
                    FRAGMENT_TAG);
            return;
        }
        throw new NullPointerException("activity and fragment both are null");
    }
}
