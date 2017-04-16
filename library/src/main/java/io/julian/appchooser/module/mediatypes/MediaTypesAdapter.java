package io.julian.appchooser.module.mediatypes;

import android.content.Context;
import android.view.View;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import io.julian.appchooser.data.MediaType;

import static io.julian.appchooser.util.Preconditions.checkNotNull;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午9:35
 */

public class MediaTypesAdapter extends CommonAdapter<MediaType> {

    private OnMediaTypesListener mOnMediaTypesListener;

    public MediaTypesAdapter(Context context, OnMediaTypesListener onMediaTypesListener) {
        super(context, android.R.layout.simple_list_item_1, new ArrayList<MediaType>());
        mOnMediaTypesListener = checkNotNull(onMediaTypesListener);
    }

    @Override
    protected void convert(ViewHolder viewHolder, final MediaType item, int position) {
        viewHolder.setText(android.R.id.text1, item.getDisplayName());
        viewHolder.setOnClickListener(android.R.id.text1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnMediaTypesListener.onMediaType(item);
            }
        });
    }

    public void replaceData(List<MediaType> mediaTypes) {
        mDatas.clear();
        mDatas.addAll(mediaTypes);
    }
}