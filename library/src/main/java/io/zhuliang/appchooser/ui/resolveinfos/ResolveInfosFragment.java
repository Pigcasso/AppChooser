package io.zhuliang.appchooser.ui.resolveinfos;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.zhuliang.appchooser.R;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.ui.base.BaseDialogFragment;
import io.zhuliang.appchooser.ui.base.CommonAdapter;
import io.zhuliang.appchooser.ui.base.ViewHolder;

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
        ResolveInfosAdapter adapter = new ResolveInfosAdapter(getContext(), resolveInfos);
        adapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object item, int position) {
                mPresenter.openResolveInfo((ResolveInfo) item);
            }
        });
        getRecyclerView().setAdapter(adapter);
    }

    @Override
    public void showActivity(Intent intent, boolean fromActivity) throws ActivityNotFoundException {
        Preconditions.checkNotNull(getActivity());
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
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

        private ResolveInfosAdapter(Context context, List<ResolveInfo> datas) {
            super(context, R.layout.item_resolve_info, datas);
            mPackageManager = context.getPackageManager();
        }

        @Override
        protected void convert(ViewHolder holder, final ResolveInfo resolveInfo, int position) {
            holder.setImageDrawable(R.id.image_view_resolver_icon,
                    resolveInfo.loadIcon(mPackageManager));
            holder.setText(R.id.text_view_resolve_display_name,
                    resolveInfo.loadLabel(mPackageManager).toString());
        }
    }

    protected abstract RecyclerView getRecyclerView();

    protected abstract ProgressBar getProgressBar();
}
