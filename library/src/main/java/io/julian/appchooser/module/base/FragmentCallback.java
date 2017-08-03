package io.julian.appchooser.module.base;

import android.app.Dialog;
import android.os.Bundle;

/**
 * @author Zhu Liang
 * @version 1.0
 * @since 2017/4/15 下午5:50
 */

public interface FragmentCallback {

    void onCreate(Bundle savedInstanceState);

    void onDestroy();

    Dialog onCreateDialog(Bundle savedInstanceState);

    void onDestroyView();

    void onStart();

    void onStop();

    void onResume();

    void onPause();


}
