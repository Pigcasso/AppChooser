package io.julian.appchooser.sample.module.fileinfos;

import android.content.Context;
import android.view.View;

import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.julian.appchooser.sample.R;
import io.julian.appchooser.sample.data.FileInfo;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午3:28
 */

public class FileInfosAdapter extends CommonAdapter<FileInfo> {
    public FileInfosAdapter(Context context, List<FileInfo> datas) {
        super(context, R.layout.item_file_info, datas);
    }

    @Override
    protected void convert(ViewHolder holder, final FileInfo fileInfo, int position) {
        holder.setText(R.id.text_view_file_info_name, fileInfo.getName());
        if (fileInfo.isFile()) {
            holder.setImageResource(R.id.image_view_file_info_icon, R.drawable.ic_insert_drive_file_pink_a400_36dp);
        } else {
            holder.setImageResource(R.id.image_view_file_info_icon, R.drawable.ic_folder_light_blue_500_36dp);
        }
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(fileInfo);
            }
        });
    }

    public void replaceDatas(List<FileInfo> fileInfos) {
        mDatas.clear();
        mDatas.addAll(fileInfos);
    }
}
