package io.zhuliang.appchooser.ui.send;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import io.zhuliang.appchooser.BuildConfig;
import io.zhuliang.appchooser.Injection;
import io.zhuliang.appchooser.R;
import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.data.ResolveInfosRepository;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.ui.resolveinfos.ResolveInfosFragment;
import io.zhuliang.appchooser.util.Logger;
import io.zhuliang.appchooser.util.ToastUtils;

/**
 * <pre>
 *     author : ZhuLiang
 *     time   : 2019/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class SendFragment extends ResolveInfosFragment {
    private static final String TAG = "SendFragment";
    static final String EXTRA_ACTION_CONFIG = BuildConfig.APPLICATION_ID + ".fragment.extra.ACTION_CONFIG";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private ResolveInfosRepository mResolveInfosRepository;

    public static SendFragment newInstance(ActionConfig actionConfig) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACTION_CONFIG, actionConfig);
        SendFragment fragment = new SendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mResolveInfosRepository = Injection.provideResolveInfosRepository(context);
        Bundle arguments = Preconditions.checkNotNull(getArguments());
        ActionConfig actionConfig = arguments.getParcelable(EXTRA_ACTION_CONFIG);
        Preconditions.checkNotNull(actionConfig);
        mActionConfig = Preconditions.checkNotNull(actionConfig);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getContext();
        Preconditions.checkNotNull(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_send, null);
        mRecyclerView = contentView.findViewById(R.id.recycler_view);
        mProgressBar = contentView.findViewById(R.id.progress_bar);
        AlertDialog.Builder bottomSheetDialog = new AlertDialog.Builder(context);
        bottomSheetDialog.setView(contentView)
                .setTitle(R.string.send_title);
        return bottomSheetDialog.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mResolveInfos == null || mResolveInfos.isEmpty()) {
            loadResolveInfos();
        }
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    protected void openResolveInfo(ResolveInfo resolveInfo) {
        Intent intent = new Intent(mActionConfig.actionName);
        intent.putExtra(Intent.EXTRA_TEXT, mActionConfig.text);
        intent.setType(mActionConfig.mimeType);
        intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name));
        try {
            showActivity(intent, mActionConfig.fromActivity);
        } catch (ActivityNotFoundException e) {
            showNoResolveInfos();
        }
        dismissSelf();
    }

    @Override
    protected void showNoResolveInfos() {
        Activity activity = Preconditions.checkNotNull(getActivity());
        ToastUtils.showToast(activity, R.string.send_no_apps_can_share_text);
    }

    private void loadResolveInfos() {
        Logger.d(TAG, "loadResolveInfos: START");
        setLoadingIndicator(true);
        Intent intent = new Intent(mActionConfig.actionName);
        intent.putExtra(Intent.EXTRA_TEXT, mActionConfig.text);
        intent.setType(mActionConfig.mimeType);
        List<ResolveInfo> resolveInfos = mResolveInfosRepository.listResolveInfos(intent);
        if (resolveInfos.isEmpty()) {
            showNoResolveInfos();
        } else {
            showResolveInfos(resolveInfos);
        }
        setLoadingIndicator(false);
        Logger.d(TAG, "loadResolveInfos: END");
    }
}
