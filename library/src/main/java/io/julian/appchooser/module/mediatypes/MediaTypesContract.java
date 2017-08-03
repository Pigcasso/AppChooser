package io.julian.appchooser.module.mediatypes;

import java.util.List;

import io.julian.appchooser.data.MediaType;
import io.julian.mvp.BasePresenter;
import io.julian.mvp.BaseView;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午5:42
 */

interface MediaTypesContract {

    interface View extends BaseView<Presenter> {
        void showMediaTypes(List<MediaType> mediaTypes);
    }

    interface Presenter extends BasePresenter {
        void loadMediaTypes();
    }
}
