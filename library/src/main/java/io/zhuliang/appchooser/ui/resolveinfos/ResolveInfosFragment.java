package io.zhuliang.appchooser.ui.resolveinfos;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import io.zhuliang.appchooser.R;
import io.zhuliang.appchooser.action.ActionConfig;
import io.zhuliang.appchooser.data.ActivityInfo;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.ui.base.CommonAdapter;
import io.zhuliang.appchooser.ui.base.ViewHolder;

public abstract class ResolveInfosFragment extends DialogFragment {
    protected ActionConfig mActionConfig;
    protected List<ResolveInfo> mResolveInfos;

    public void setLoadingIndicator(boolean active) {
        if (active) {
            getRecyclerView().setVisibility(View.INVISIBLE);
            getProgressBar().setVisibility(View.VISIBLE);
        } else {
            getRecyclerView().setVisibility(View.VISIBLE);
            getProgressBar().setVisibility(View.INVISIBLE);
        }
    }

    public void showResolveInfos(@NonNull List<ResolveInfo> resolveInfos) {
        mResolveInfos = resolveInfos;
        ResolveInfosAdapter adapter = new ResolveInfosAdapter(getContext(), resolveInfos);
        adapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object item, int position) {
                openResolveInfo((ResolveInfo) item);
            }
        });
        getRecyclerView().setAdapter(adapter);
    }

    protected void showActivity(Intent intent, boolean fromActivity) throws ActivityNotFoundException {
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

    protected abstract RecyclerView getRecyclerView();

    protected abstract ProgressBar getProgressBar();

    protected abstract void openResolveInfo(ResolveInfo resolveInfo);

    protected abstract void showNoResolveInfos();

    protected boolean containsInExcluded(@Nullable ActivityInfo activityInfo) {
        if (activityInfo == null) {
            return false;
        }
        if (mActionConfig.excluded == null) {
            return false;
        }
        for (ComponentName componentName : mActionConfig.excluded) {
            if (componentName.getPackageName().equals(activityInfo.getPkg())
                    && componentName.getClassName().equals(activityInfo.getCls())) {
                return true;
            }
        }
        return false;
    }

    protected boolean containsInExcluded(@Nullable ResolveInfo resolveInfo) {
        if (resolveInfo == null) {
            return false;
        }
        if (mActionConfig.excluded == null) {
            return false;
        }
        if (resolveInfo.activityInfo == null) {
            return false;
        }
        String cls = resolveInfo.activityInfo.name;
        String pkg = resolveInfo.activityInfo.packageName;
        for (ComponentName componentName : mActionConfig.excluded) {
            if (componentName.getPackageName().equals(pkg)
                    && componentName.getClassName().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    protected void dismissSelf() {
        dismiss();
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
}
