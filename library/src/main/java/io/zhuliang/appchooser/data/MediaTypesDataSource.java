package io.zhuliang.appchooser.data;

import java.util.List;

import rx.Observable;

/**
 * {@link MediaType} 数据源
 *
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午1:47
 */

public interface MediaTypesDataSource {

    Observable<List<MediaType>> listMediaTypes();
}
