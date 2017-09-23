package io.julian.appchooser.action;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import io.julian.appchooser.AppChooser;
import io.julian.appchooser.BuildConfig;
import io.julian.appchooser.ui.send.SendFragment;
import io.julian.appchooser.util.MimeType;

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
        if (activity == null) {
            return;
        }
        Fragment fragment = mAppChooser.getFragment();
        if (fragment != null) {
            mActionConfig.fromActivity = false;
            SendFragment.newInstance(mActionConfig).show(fragment.getFragmentManager(),
                    FRAGMENT_TAG);
        } else {
            mActionConfig.fromActivity = true;
            SendFragment.newInstance(mActionConfig).show(activity.getSupportFragmentManager(),
                    FRAGMENT_TAG);
        }
    }
}
