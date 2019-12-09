package io.zhuliang.appchooser.action;

import android.content.ComponentName;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import io.zhuliang.appchooser.AppChooser;
import io.zhuliang.appchooser.BuildConfig;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.ui.send.SendFragment;
import io.zhuliang.appchooser.util.MimeType;

/**
 * @author Zhu Liang
 */

public class SendAction {

    private static final String FRAGMENT_TAG = BuildConfig.APPLICATION_ID + ".fragment.tag.SEND";

    private AppChooser mAppChooser;
    private ActionConfig mActionConfig = new ActionConfig();

    public SendAction(AppChooser appChooser) {
        mAppChooser = appChooser;
        mActionConfig.actionName = Intent.ACTION_SEND;
        mActionConfig.mimeType = MimeType.TEXT_PLAIN;
    }

    public SendAction text(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }
        mActionConfig.text = text;
        return this;
    }

    public SendAction excluded(ComponentName... componentNames) {
        mActionConfig.excluded = componentNames;
        return this;
    }

    public void load() {
        FragmentActivity activity = mAppChooser.getActivity();
        if (activity != null) {
            mActionConfig.fromActivity = true;
            SendFragment.newInstance(mActionConfig).show(activity.getSupportFragmentManager(),
                    FRAGMENT_TAG);
            return;
        }
        Fragment fragment = mAppChooser.getFragment();
        if (fragment != null) {
            mActionConfig.fromActivity = false;
            FragmentManager fragmentManager =
                    Preconditions.checkNotNull(fragment.getFragmentManager());
            SendFragment.newInstance(mActionConfig).show(fragmentManager,
                    FRAGMENT_TAG);
            return;
        }
        throw new NullPointerException("activity and fragment both are null");
    }
}
