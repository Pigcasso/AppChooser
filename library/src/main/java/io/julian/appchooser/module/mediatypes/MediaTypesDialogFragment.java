package io.julian.appchooser.module.mediatypes;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.julian.appchooser.R;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.media_types_title);
        return mDelegate.onCreateView(inflater, container, savedInstanceState);
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
