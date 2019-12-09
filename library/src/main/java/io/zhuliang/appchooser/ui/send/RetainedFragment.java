package io.zhuliang.appchooser.ui.send;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.zhuliang.appchooser.Injection;
import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.data.ResolveInfosRepository;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.util.Logger;

/**
 * <pre>
 *     author : ZhuLiang
 *     time   : 2019/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class RetainedFragment extends Fragment {
    private static final String TAG = "RetainedFragment";
    @Nullable
    private ResolveInfosAsyncTask mAsyncTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setFragment(@Nullable SendFragment fragment) {
        if (mAsyncTask == null) {
            if (fragment != null) {
                mAsyncTask = new ResolveInfosAsyncTask(fragment);
                Bundle arguments = Preconditions.checkNotNull(fragment.getArguments());
                mAsyncTask.execute((ActionConfig) arguments.getParcelable(SendFragment.EXTRA_ACTION_CONFIG));
            }
        } else {
            mAsyncTask.setSendFragment(fragment);
        }
    }

    private static class ResolveInfosAsyncTask extends AsyncTask<ActionConfig, Void, List<ResolveInfo>> {
        @Nullable
        private SendFragment mSendFragment;
        @NonNull
        private ResolveInfosRepository mRepository;
        private List<ResolveInfo> mResolveInfos;

        private ResolveInfosAsyncTask(SendFragment fragment) {
            mSendFragment = Preconditions.checkNotNull(fragment);
            Context context = Preconditions.checkNotNull(fragment.getContext());
            mRepository = Injection.provideResolveInfosRepository(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSendFragment != null) {
                mSendFragment.setLoadingIndicator(true);
            }
        }

        @Override
        protected List<ResolveInfo> doInBackground(ActionConfig... actionConfigs) {
            Logger.d(TAG, "doInBackground: START");
            ActionConfig actionConfig = actionConfigs[0];
            Intent intent = new Intent(actionConfig.actionName);
            intent.putExtra(Intent.EXTRA_TEXT, actionConfig.text);
            intent.setType(actionConfig.mimeType);
            List<ResolveInfo> resolveInfos = mRepository.listResolveInfos(intent);
            Logger.d(TAG, "doInBackground: END");
            return resolveInfos;
        }

        @Override
        protected void onPostExecute(List<ResolveInfo> resolveInfos) {
            super.onPostExecute(resolveInfos);
            mResolveInfos = resolveInfos;
            if (mSendFragment != null) {
                setSendFragment(mSendFragment);
            }
        }

        private void setSendFragment(@Nullable SendFragment sendFragment) {
            if (sendFragment == null) {
                Preconditions.checkNotNull(mSendFragment);
                mSendFragment.setLoadingIndicator(false);
                mSendFragment = null;
            } else {
                if (mResolveInfos != null) {
                    sendFragment.setLoadingIndicator(false);
                    sendFragment.showResolveInfos(mResolveInfos);
                }
                mSendFragment = sendFragment;
            }
        }
    }
}
