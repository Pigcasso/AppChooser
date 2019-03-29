package io.zhuliang.appchooser.ui.resolveinfos;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import io.zhuliang.appchooser.R;
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

    private static class ResolveInfosAdapter extends CommonAdapter<ResolveInfo> {

        private PackageManager mPackageManager;
        private OnResolveInfoListener mOnResolveInfoListener;

        private ResolveInfosAdapter(Context context, List<ResolveInfo> datas,
                                    OnResolveInfoListener onResolveInfoListener) {
            super(context, R.layout.item_resolve_info, datas);
            mPackageManager = context.getPackageManager();
            mOnResolveInfoListener = onResolveInfoListener;
        }

        @Override
        protected void convert(ViewHolder holder, final ResolveInfo resolveInfo, int position) {
            holder.setImageDrawable(R.id.image_view_resolver_icon,
                    resolveInfo.loadIcon(mPackageManager));
            holder.setText(R.id.text_view_resolve_display_name,
                    resolveInfo.loadLabel(mPackageManager).toString());

            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
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
