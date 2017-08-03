package io.julian.appchooser.module.resolvers.v4;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.module.resolvers.ResolversDelegate;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/16 上午11:30
 */

public class ResolversDialogFragment extends DialogFragment {

    private static final String KEY_RESOLVERS = "key.resolvers";
    private ResolversDelegate mDelegate;

    public static ResolversDialogFragment newInstance(List<Resolver> resolvers) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_RESOLVERS, new ArrayList<Parcelable>(resolvers));
        ResolversDialogFragment fragment = new ResolversDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate = new ResolversDelegate(getActivity(), getArguments().<Resolver>getParcelableArrayList(KEY_RESOLVERS));
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
