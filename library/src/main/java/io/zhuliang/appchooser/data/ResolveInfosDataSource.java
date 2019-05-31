package io.zhuliang.appchooser.data;

import android.content.Intent;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @author Zhu Liang
 */

public interface ResolveInfosDataSource {

    @NonNull
    List<ResolveInfo> listResolveInfos(@NonNull Intent intent);
}
