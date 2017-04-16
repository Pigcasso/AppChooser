package io.julian.appchooser.module.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午5:50
 */

public interface FragmentCallback {

    void onCreate(Bundle savedInstanceState);

    void onDestroy();

    @Nullable
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onDestroyView();

    void onStart();

    void onStop();

    void onResume();

    void onPause();


}
