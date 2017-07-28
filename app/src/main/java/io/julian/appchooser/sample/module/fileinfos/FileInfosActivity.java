package io.julian.appchooser.sample.module.fileinfos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import io.julian.appchooser.AppChooser;
import io.julian.appchooser.sample.R;
import io.julian.appchooser.sample.constant.FileConsts;
import io.julian.appchooser.sample.data.FileInfo;

public class FileInfosActivity extends AppCompatActivity {
    private static final String TAG = FileInfosActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 110;
    private AppChooser mAppChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_infos);

        mAppChooser = AppChooser.with(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                showRootDirectory();
            }
        } else {
            showRootDirectory();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mAppChooser.bind();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        mAppChooser.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Log.i(TAG, "onRequestPermissionsResult granted=" + granted);
            if (granted) {
                showRootDirectory();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_file_infos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_file_infos_clear_defaults:
                mAppChooser.cleanDefaults();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileInfo(FileInfo fileInfo) {
        if (fileInfo.isFile()) {
            showFile(fileInfo);
        } else {
            showDirectory(fileInfo);
        }
    }

    private void showFile(FileInfo file) {
        mAppChooser.file(new File(file.getAbsolutePath())).load();
    }

    private void showDirectory(FileInfo directory) {
        FileInfosFragment fragment = FileInfosFragment.newInstance(directory.getAbsolutePath());
        getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showRootDirectory() {

        FileInfosFragment fragment =
                (FileInfosFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = FileInfosFragment.newInstance(Environment.getExternalStorageDirectory().getAbsolutePath());
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, fragment)
                    .commit();
        }
    }
}
