package io.zhuliang.appchooser.ui.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.zhuliang.appchooser.internal.Preconditions;

/**
 * <pre>
 *     author : ZhuLiang
 *     time   : 2019/03/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    @NonNull
    private Context mContext;
    private int mLayoutId;
    protected List<T> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public CommonAdapter(@NonNull Context context, int layoutId, @NonNull List<T> datas) {
        mContext = Preconditions.checkNotNull(context);
        mLayoutId = layoutId;
        mDatas = Preconditions.checkNotNull(datas);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = ViewHolder.createViewHolder(mContext, parent, mLayoutId);
        setListener(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        convert(holder, mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public List<T> getDatas() {
        return mDatas;
    }

    protected abstract void convert(ViewHolder holder, final T item, int position);

    private void setListener(final ViewHolder holder) {
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, getDatas().get(position), position);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Object item, int position);
    }
}
