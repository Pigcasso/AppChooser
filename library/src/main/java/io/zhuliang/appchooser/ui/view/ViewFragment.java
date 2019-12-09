package io.zhuliang.appchooser.ui.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import io.zhuliang.appchooser.BuildConfig;
import io.zhuliang.appchooser.Injection;
import io.zhuliang.appchooser.R;
import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.data.ActivityInfo;
import io.zhuliang.appchooser.data.ActivityInfosRepository;
import io.zhuliang.appchooser.data.MediaType;
import io.zhuliang.appchooser.data.MediaTypesRepository;
import io.zhuliang.appchooser.data.ResolveInfosRepository;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.ui.base.CommonAdapter;
import io.zhuliang.appchooser.ui.base.ViewHolder;
import io.zhuliang.appchooser.ui.resolveinfos.ResolveInfosFragment;
import io.zhuliang.appchooser.util.FileUtils;
import io.zhuliang.appchooser.util.Logger;
import io.zhuliang.appchooser.util.MimeType;
import io.zhuliang.appchooser.util.ToastUtils;

/**
 * <pre>
 *     author : ZhuLiang
 *     time   : 2019/03/30
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ViewFragment extends ResolveInfosFragment {
    private static final String TAG = "ViewFragment";

    private static final String EXTRA_ACTION_CONFIG = BuildConfig.APPLICATION_ID + ".fragment.extra.ACTION_CONFIG";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private FrameLayout mCheckBoxContainer;
    private CheckBox mCheckBox;

    private ActivityInfosRepository mActivityInfosRepository;
    private MediaTypesRepository mMediaTypesRepository;
    private ResolveInfosRepository mResolveInfosRepository;

    public static ViewFragment newInstance(ActionConfig actionConfig) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACTION_CONFIG, actionConfig);
        ViewFragment fragment = new ViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Bundle arguments = Preconditions.checkNotNull(getArguments());
        ActionConfig actionConfig = arguments.getParcelable(EXTRA_ACTION_CONFIG);
        Preconditions.checkNotNull(actionConfig);
        File file = new File(actionConfig.pathname);
        FileUtils.checkFile(file);
        String mimeType = MimeType.getMimeType(file);
        if (mimeType == null) {
            mimeType = MimeType.ALL;
        }
        actionConfig.mimeType = mimeType;
        mActionConfig = actionConfig;

        mActivityInfosRepository = Injection.provideActivityInfosRepository(context);
        mMediaTypesRepository = Injection.provideMediaTypesRepository(context);
        mResolveInfosRepository = Injection.provideResolveInfosRepository(context);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = Preconditions.checkNotNull(getContext());
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
        mRecyclerView = contentView.findViewById(R.id.recycler_view);
        mProgressBar = contentView.findViewById(R.id.progress_bar);
        mCheckBoxContainer = contentView.findViewById(R.id.frame_resolvers_check_container);
        mCheckBox = contentView.findViewById(R.id.check_resolvers_set_as_default);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(contentView)
                .setTitle(R.string.view_title);
        return alertDialog.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mResolveInfos == null || mResolveInfos.isEmpty()) {
            loadActivityInfo();
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
        ActivityInfo activityInfo = new ActivityInfo(mActionConfig.mimeType,
                resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        if (mCheckBox.isChecked()) {
            mActivityInfosRepository.saveActivityInfo(activityInfo);
        }
        showActivityInternal(activityInfo);
        dismissSelf();
    }

    @Override
    protected void showNoResolveInfos() {
        Activity activity = Preconditions.checkNotNull(getActivity());
        ToastUtils.showToast(activity, R.string.view_no_apps_can_open_this_file);
    }

    private void loadActivityInfo() {
        ActivityInfo activityInfo = mActivityInfosRepository.getActivityInfo(mActionConfig.mimeType);
        // 如果 activityInfo 包含在"排除组件列表"中
        if (activityInfo != null && containsInExcluded(activityInfo)) {
            activityInfo = null;
        }
        if (activityInfo == null) {
            loadMediaTypesOrResolveInfos();
        } else {
            try {
                showActivityInternal(activityInfo);
            } catch (ActivityNotFoundException e) {
                loadMediaTypesOrResolveInfos();
            }
        }
    }

    private void loadMediaTypesOrResolveInfos() {
        Logger.d(TAG, "loadMediaTypesOrResolveInfos: START");
        if (MimeType.ALL.equals(mActionConfig.mimeType)) {
            loadMediaTypes();
        } else {
            loadResolveInfos();
        }
        Logger.d(TAG, "loadMediaTypesOrResolveInfos: END");
    }

    private void loadMediaTypes() {
        Logger.d(TAG, "loadMediaTypes: START");
        setLoadingIndicator(true);
        List<MediaType> mediaTypes = mMediaTypesRepository.listMediaTypes();
        showMediaTypes(mediaTypes);
        setLoadingIndicator(false);
        Logger.d(TAG, "loadMediaTypes: END");
    }

    private void loadResolveInfos() {
        Logger.d(TAG, "loadResolveInfos: START");
        setLoadingIndicator(true);
        Intent intent = new Intent(mActionConfig.actionName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Context context = Preconditions.checkNotNull(getContext());
            Uri uri = FileProvider.getUriForFile(context, mActionConfig.authority,
                    new File(mActionConfig.pathname));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, mActionConfig.mimeType);
        } else {
            Uri uri = Uri.fromFile(new File(mActionConfig.pathname));
            intent.setDataAndType(uri, mActionConfig.mimeType);
        }
        List<ResolveInfo> resolveInfos = mResolveInfosRepository.listResolveInfos(intent);
        // 将包含在"排除组件列表"中的 resolveInfo 移除
        Iterator<ResolveInfo> iterator = resolveInfos.iterator();
        while (iterator.hasNext()) {
            if (containsInExcluded(iterator.next())) {
                iterator.remove();
            }
        }

        if (resolveInfos.isEmpty()) {
            showNoResolveInfos();
        } else {
            showResolveInfos(resolveInfos);
        }
        setCheckBoxIndicator(true);
        setLoadingIndicator(false);
        Logger.d(TAG, "loadResolveInfos: END");
    }

    private void showMediaTypes(List<MediaType> mediaTypes) {
        setCheckBoxIndicator(false);
        Context context = Preconditions.checkNotNull(getContext());
        CommonAdapter mediaTypesAdapter = new CommonAdapter<MediaType>(context, R.layout.item_media_type, mediaTypes) {
            @Override
            protected void convert(ViewHolder holder, MediaType item, int position) {
                holder.setText(R.id.text_view_media_type_name, item.getDisplayName());
            }
        };
        mediaTypesAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object item, int position) {
                openMediaType((MediaType) item);
            }
        });
        mRecyclerView.setAdapter(mediaTypesAdapter);
    }

    private void openMediaType(MediaType mediaType) {
        mActionConfig.mimeType = mediaType.getMimeType();
        loadResolveInfos();
    }

    private void showActivityInternal(@NonNull ActivityInfo activityInfo)
            throws ActivityNotFoundException {
        Preconditions.checkNotNull(activityInfo);
        if (mActionConfig.requestCode == -1) {
            showActivity(makeIntent(activityInfo), mActionConfig.fromActivity);
        } else {
            showActivity(makeIntent(activityInfo), mActionConfig.requestCode,
                    mActionConfig.fromActivity);
        }
    }

    private Intent makeIntent(@NonNull ActivityInfo activityInfo) {
        Preconditions.checkNotNull(activityInfo);
        Context context = Preconditions.checkNotNull(getContext());
        Intent intent = new Intent(mActionConfig.actionName);
        intent.setComponent(new ComponentName(activityInfo.getPkg(), activityInfo.getCls()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(context, mActionConfig.authority,
                    new File(mActionConfig.pathname));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, mActionConfig.mimeType);
        } else {
            Uri uri = Uri.fromFile(new File(mActionConfig.pathname));
            intent.setDataAndType(uri, mActionConfig.mimeType);
        }
        return intent;
    }

    private void showActivity(Intent intent, int requestCode, boolean fromActivity)
            throws ActivityNotFoundException {
        Activity activity = getActivity();
        Preconditions.checkNotNull(activity);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            if (fromActivity) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                startActivityForResult(intent, requestCode);
            }
        } else {
            throw new ActivityNotFoundException();
        }
    }

    private void setCheckBoxIndicator(boolean visible) {
        mCheckBoxContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
