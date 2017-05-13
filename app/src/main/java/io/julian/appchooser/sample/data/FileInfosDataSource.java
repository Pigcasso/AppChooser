package io.julian.appchooser.sample.data;

import java.util.List;

import rx.Observable;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/5/13 下午2:39
 */

public interface FileInfosDataSource {

    Observable<List<FileInfo>> listFileInfos(FileInfo directory);
}
