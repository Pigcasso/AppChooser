package io.julian.appchooser.ui.send;

import io.julian.appchooser.ui.resolveinfos.ResolveInfosContract;

/**
 * @author Zhu Liang
 */

interface SendContract {

    interface View extends ResolveInfosContract.View<Presenter> {

    }

    interface Presenter extends ResolveInfosContract.Presenter {

    }
}
