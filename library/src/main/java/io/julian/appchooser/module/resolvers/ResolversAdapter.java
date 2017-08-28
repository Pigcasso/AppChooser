package io.julian.appchooser.module.resolvers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.View;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import io.julian.appchooser.R;
import io.julian.appchooser.data.Resolver;

import static io.julian.common.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 上午11:50
 */
public class ResolversAdapter extends CommonAdapter<Resolver> {

    @NonNull
    private OnResolversListener mOnResolversListener;
    @NonNull
    private PackageManager mPackageManager;

    public ResolversAdapter(Context context, List<Resolver> datas, OnResolversListener onResolversListener) {
        super(context, R.layout.item_resolver, datas);
        mPackageManager = context.getPackageManager();
        mOnResolversListener = checkNotNull(onResolversListener);
    }

    @Override
    protected void convert(ViewHolder holder, final Resolver resolver, int position) {
        holder.setImageDrawable(R.id.image_view_resolver_icon, resolver.loadIcon(mPackageManager));
        holder.setText(R.id.text_view_resolve_display_name, resolver.loadLabel(mPackageManager));
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnResolversListener.onResolver(resolver);
            }
        });
    }
}
