package io.julian.appchooser.module.mediatypes.v4;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import io.julian.appchooser.module.mediatypes.MediaTypesDelegate;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午5:47
 */

public class MediaTypesDialogFragment extends DialogFragment {

    private MediaTypesDelegate mDelegate;

    public static MediaTypesDialogFragment newInstance() {

        Bundle args = new Bundle();

        MediaTypesDialogFragment fragment = new MediaTypesDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate = new MediaTypesDelegate(getActivity());
        mDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDelegate.onDestroy();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDelegate.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDelegate.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mDelegate.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDelegate.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDelegate.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDelegate.onPause();
    }
}
