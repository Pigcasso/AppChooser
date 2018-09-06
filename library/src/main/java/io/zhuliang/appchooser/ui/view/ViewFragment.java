package io.zhuliang.appchooser.ui.view;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import io.zhuliang.appchooser.BuildConfig;
import io.zhuliang.appchooser.Injection;
import io.zhuliang.appchooser.R;
import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.data.MediaType;
import io.zhuliang.appchooser.data.RecommendApp;
import io.zhuliang.appchooser.data.ResolveInfosRepository;
import io.zhuliang.appchooser.ui.resolveinfos.ResolveInfosFragment;
import io.zhuliang.appchooser.util.AppUtils;

import static io.zhuliang.appchooser.internal.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 */

public class ViewFragment extends ResolveInfosFragment<ViewContract.Presenter>
        implements ViewContract.View {

    private static final String EXTRA_ACTION_CONFIG = BuildConfig.APPLICATION_ID + ".fragment.extra.ACTION_CONFIG";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private FrameLayout mCheckBoxContainer;
    private CheckBox mCheckBox;
    private ActionConfig actionConfig;

    public static ViewFragment newInstance(ActionConfig actionConfig) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ACTION_CONFIG, actionConfig);
        ViewFragment fragment = new ViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionConfig = null;
        if (getArguments() != null) {
            actionConfig = getArguments().getParcelable(EXTRA_ACTION_CONFIG);
        }
        if (actionConfig == null) {
            throw new NullPointerException("actionConfig == null");
        }
        Context context = getContext();
        new ViewPresenter(this, Injection.provideSchedulerProvider(), actionConfig,
                new ResolveInfosRepository(context, Injection.provideSchedulerProvider()),
                Injection.provideActivityInfosRepository(getContext()),
                Injection.provideMediaTypesRepository(getContext()));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) contentView.findViewById(R.id.progress_bar);
        mCheckBoxContainer = (FrameLayout) contentView.findViewById(R.id.frame_resolvers_check_container);
        mCheckBox = (CheckBox) contentView.findViewById(R.id.check_resolvers_set_as_default);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(contentView)
                .setTitle(R.string.view_title);
        return alertDialog.create();
    }

    @Override
    public void showActivity(Intent intent, int requestCode, boolean fromActivity) throws ActivityNotFoundException {
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            if (fromActivity) {
                getActivity().startActivityForResult(intent, requestCode);
            } else {
                startActivityForResult(intent, requestCode);
            }
        } else {
            throw new ActivityNotFoundException();
        }
    }

    @Override
    public void showResolveInfos(@NonNull List<ResolveInfo> resolveInfos) {
        mCheckBoxContainer.setVisibility(View.VISIBLE);
        super.showResolveInfos(resolveInfos);
    }

    @Override
    public void showRecommendApp(@NonNull final RecommendApp app) {
        getRecyclerView().post(new Runnable() {
            @Override
            public void run() {
                ResolveInfosAdapter adapter = (ResolveInfosAdapter) getRecyclerView().getAdapter();
                adapter.addRecommendApp(actionConfig, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickRecommend(actionConfig);
                        dismissDialog();
                    }
                });
                getRecyclerView().scrollToPosition(0);
            }
        });
    }

    private void onClickRecommend(ActionConfig config) {
        if (getContext() == null) return;

        if (AppUtils.isPackageInstalled(getContext(), config.mRecommendApp.packageName)) {
            AppUtils.openWithRecommendApp(getContext(), config);
        } else {
            AppUtils.browse(getContext(), config.mRecommendApp.downloadUrl);
        }
    }

    @Override
    public void showNoResolveInfos() {
        Toast.makeText(getActivity(), getString(R.string.view_no_apps_can_open_this_file),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMediaTypes(List<MediaType> mediaTypes) {
        mCheckBoxContainer.setVisibility(View.GONE);
        mRecyclerView.setAdapter(new MediaTypesAdapter(mediaTypes,
                new OnMediaTypesListener() {
                    @Override
                    public void onMediaType(MediaType mediaType) {
                        mPresenter.openMediaType(mediaType);
                    }
                }));
    }

    @Override
    public boolean isAsDefault() {
        return mCheckBox.isChecked();
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return mProgressBar;
    }

    private static class MediaTypesAdapter extends BaseQuickAdapter<MediaType, BaseViewHolder> {

        private OnMediaTypesListener mOnMediaTypesListener;

        private MediaTypesAdapter(List<MediaType> datas,
                                  OnMediaTypesListener onMediaTypesListener) {
            super(R.layout.item_media_type, datas);
            mOnMediaTypesListener = checkNotNull(onMediaTypesListener);
        }

        @Override
        protected void convert(BaseViewHolder viewHolder, final MediaType item) {
            viewHolder.setText(R.id.text_view_media_type_name, item.getDisplayName());
            viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnMediaTypesListener.onMediaType(item);
                }
            });
        }
    }
}
