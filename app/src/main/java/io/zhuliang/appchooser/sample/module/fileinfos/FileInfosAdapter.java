package io.zhuliang.appchooser.sample.module.fileinfos;

import android.content.Context;

import java.util.List;

import io.zhuliang.appchooser.sample.R;
import io.zhuliang.appchooser.sample.data.FileInfo;
import io.zhuliang.appchooser.ui.base.CommonAdapter;
import io.zhuliang.appchooser.ui.base.ViewHolder;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午3:28
 */

public class FileInfosAdapter extends CommonAdapter<FileInfo> {
    FileInfosAdapter(Context context, List<FileInfo> datas) {
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
    }

    void replaceDatas(List<FileInfo> fileInfos) {
        mDatas.clear();
        mDatas.addAll(fileInfos);
    }
}