package io.zhuliang.appchooser.ui.resolveinfos;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import io.zhuliang.appchooser.R;
import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.ui.base.BaseDialogFragment;

public abstract class ResolveInfosFragment<P extends ResolveInfosContract.Presenter>
        extends BaseDialogFragment<P> implements ResolveInfosContract.View<P> {

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            getRecyclerView().setVisibility(View.INVISIBLE);
            getProgressBar().setVisibility(View.VISIBLE);
        } else {
            getRecyclerView().setVisibility(View.VISIBLE);
            getProgressBar().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showResolveInfos(@NonNull List<ResolveInfo> resolveInfos) {
        getRecyclerView().setAdapter(new ResolveInfosAdapter(getContext(), resolveInfos,
                new OnResolveInfoListener() {
                    @Override
                    public void onResolveInfoClick(ResolveInfo resolveInfo) {
                        mPresenter.openResolveInfo(resolveInfo);
                    }
                }));
    }

    @Override
    public void showActivity(Intent intent, boolean fromActivity) throws ActivityNotFoundException {
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            if (fromActivity) {
                getActivity().startActivity(intent);
            } else {
                startActivity(intent);
            }
        } else {
            throw new ActivityNotFoundException();
        }
    }

    protected static class ResolveInfosAdapter extends BaseQuickAdapter<ResolveInfo, BaseViewHolder> {
        private static final String TAG = "ResolveInfosAdapter";

        private PackageManager mPackageManager;
        private OnResolveInfoListener mOnResolveInfoListener;

        private ResolveInfosAdapter(Context context, List<ResolveInfo> datas,
                                    OnResolveInfoListener onResolveInfoListener) {
            super(R.layout.item_resolve_info, datas);
            mPackageManager = context.getPackageManager();
            mOnResolveInfoListener = onResolveInfoListener;
        }

        public void addRecommendApp(final ActionConfig actionConfig, View.OnClickListener onClickListener) {
            if (actionConfig.mRecommendApp == null) return;
            View headerView = View.inflate(mContext, R.layout.item_recommend_app, null);
            ImageView icon = headerView.findViewById(R.id.image_view_resolver_icon);
            icon.setImageResource(actionConfig.mRecommendApp.iconResourceId);
            TextView name = headerView.findViewById(R.id.text_view_resolve_display_name);
            name.setText(actionConfig.mRecommendApp.name);
            TextView desc = headerView.findViewById(R.id.tv_recommend_app_desc);
            desc.setVisibility(TextUtils.isEmpty(actionConfig.mRecommendApp.description) ? View.GONE : View.VISIBLE);
            desc.setText(actionConfig.mRecommendApp.description);
            headerView.setOnClickListener(onClickListener);
            addHeaderView(headerView);
        }

        @Override
        protected void convert(BaseViewHolder holder, final ResolveInfo resolveInfo) {

            holder.setImageDrawable(R.id.image_view_resolver_icon,
                    resolveInfo.loadIcon(mPackageManager));
            holder.setText(R.id.text_view_resolve_display_name,
                    resolveInfo.loadLabel(mPackageManager).toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnResolveInfoListener != null) {
                        mOnResolveInfoListener.onResolveInfoClick(resolveInfo);
                    }
                }
            });
        }

    }

    private interface OnResolveInfoListener {
        void onResolveInfoClick(ResolveInfo resolveInfo);
    }

    protected abstract RecyclerView getRecyclerView();

    protected abstract ProgressBar getProgressBar();
}
