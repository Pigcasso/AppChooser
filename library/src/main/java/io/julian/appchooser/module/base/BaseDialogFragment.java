package io.julian.appchooser.module.base;

import android.app.DialogFragment;
import android.support.annotation.NonNull;

import io.julian.mvp.BaseDialogView;
import io.julian.mvp.BasePresenter;

public abstract class BaseDialogFragment<P extends BasePresenter> extends DialogFragment
        implements BaseDialogView<P> {
    protected P mPresenter;

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public final void setPresenter(@NonNull P presenter) {
        if (presenter == null) {
            throw new NullPointerException("presenter must not be null");
        }
        mPresenter = presenter;
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }
}
