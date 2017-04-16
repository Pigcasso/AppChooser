package io.julian.appchooser.sample;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.julian.appchooser.AppChooser;
import io.julian.appchooser.data.Resolver;
import io.julian.appchooser.module.mediatypes.MediaTypesDialogFragment;
import io.julian.appchooser.module.resolvers.ResolversDialogFragment;
import io.julian.appchooser.util.ActivityUtils;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 838;
    private AppChooser mAppChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAppChooser = AppChooser.with(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppChooser.bind();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppChooser.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Toast.makeText(MainActivity.this, "request code: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }

    public void showMediaTypesDialog(View view) {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_media_types");
        if (prev != null) {
            ft.remove(prev);
        }
        MediaTypesDialogFragment fragment = MediaTypesDialogFragment.newInstance();
        ActivityUtils.showDialog(ft, fragment, "dialog_media_types");
    }

    public void showResolversDialog(View view) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_resolvers");
        if (prev != null) {
            ft.remove(prev);
        }
        ResolversDialogFragment fragment = ResolversDialogFragment.newInstance(fakeResolvers());
        ActivityUtils.showDialog(ft, fragment, "dialog_resolvers");
    }

    @NonNull
    private ArrayList<Resolver> fakeResolvers() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(downloadDir, "appchooser.txt")), "text/plain");
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        ArrayList<Resolver> resolvers = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfos) {
            resolvers.add(new Resolver(resolveInfo));
        }
        return resolvers;
    }

    public void openTextFile(View view) {
        File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        mAppChooser.file(new File(download, "appchooser.txt")).load();
    }

    public void openXmindFile(View view) {
        File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        mAppChooser.file(new File(download, "JianguoyunForAndroid.xmind")).load();
    }

    public void cleanTheDefaults(View view) {
        mAppChooser.cleanDefaults();
    }

    public void openTextFileWithRequestCode(View view) {
        File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        mAppChooser.file(new File(download, "appchooser.txt")).requestCode(REQUEST_CODE).load();
    }
}
