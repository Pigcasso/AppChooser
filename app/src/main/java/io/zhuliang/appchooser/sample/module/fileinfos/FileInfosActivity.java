package io.zhuliang.appchooser.sample.module.fileinfos;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import io.zhuliang.appchooser.AppChooser;
import io.zhuliang.appchooser.internal.Preconditions;
import io.zhuliang.appchooser.sample.BuildConfig;
import io.zhuliang.appchooser.sample.R;
import io.zhuliang.appchooser.sample.data.FileInfo;
import io.zhuliang.appchooser.util.Logger;

public class FileInfosActivity extends AppCompatActivity implements FileInfosFragment.OnItemClickListener {
    private static final String TAG = FileInfosActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 110;
    private static final String EXTRA_SELECTED_DIRECTORY = "extra_selected_directory";
    private static final String EXTRA_DIRECTORIES = "extra_directories";

    private static final int OPERATION_NONE = 0;
    private static final int OPERATION_BACK_PRESSED = 1;
    private static final int OPERATION_SELECTED_TAB = 2;
    private static final int OPERATION_CLICK_ITEM = 3;

    private static final int REQUEST_CODE_OPEN_FILE = 10;
    private static final int REQUEST_CODE_MANAGE_EXTERNAL_STORAGE = 11;

    @IntDef(value = {OPERATION_NONE, OPERATION_BACK_PRESSED, OPERATION_SELECTED_TAB,
            OPERATION_CLICK_ITEM})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Operation {
    }

//    private AppChooser mAppChooser;

    ComponentName[] excluded = new ComponentName[]{
    };

    private ArrayList<FileInfo> mDirectories = new ArrayList<>();
    private FileInfo mSelectedDirectory;

    private FragmentManager mFragmentManager;

    private TabLayout mTabLayout;

    private MyHandler mMyHandler;

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener =
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    showDirectory((FileInfo) tab.getTag(), OPERATION_SELECTED_TAB);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            };

    public static void start(Context context) {
        Intent starter = new Intent(context, FileInfosActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mDirectories = savedInstanceState.getParcelableArrayList(EXTRA_DIRECTORIES);
            mSelectedDirectory = savedInstanceState.getParcelable(EXTRA_SELECTED_DIRECTORY);
        }

        mMyHandler = new MyHandler(this);

        mFragmentManager = getSupportFragmentManager();

        setContentView(R.layout.activity_file_infos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }*/

        mTabLayout = findViewById(R.id.tabLayout);
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);

//        mAppChooser = AppChooser.with(this).excluded(excluded);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
                        REQUEST_CODE_MANAGE_EXTERNAL_STORAGE);
            } else {
                showDirectory(null, OPERATION_NONE);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                showDirectory(null, OPERATION_NONE);
            }
        } else {
            showDirectory(null, OPERATION_NONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_DIRECTORIES, mDirectories);
        outState.putParcelable(EXTRA_SELECTED_DIRECTORY, mSelectedDirectory);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Logger.d(TAG, "onRequestPermissionsResult granted=" + granted);
            if (granted) {
                showDirectory(null, OPERATION_NONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_FILE) {
            Logger.d(TAG, "onActivityResult: " + requestCode + "," + resultCode + "," + data);
        } else if (requestCode == REQUEST_CODE_MANAGE_EXTERNAL_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    showDirectory(null, OPERATION_NONE);
                }
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
        if (item.getItemId() == R.id.menu_file_infos_clear_defaults) {
            AppChooser.from(this).cleanDefaults();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showDirectory(null, OPERATION_BACK_PRESSED);
    }

    @Override
    public void onItemClick(FileInfo fileInfo) {
        if (fileInfo.isFile()) {
            showFile(fileInfo);
        } else {
            showDirectory(fileInfo, OPERATION_CLICK_ITEM);
        }
    }

    private void showFile(FileInfo fileInfo) {
        File file = new File(fileInfo.getAbsolutePath());
        AppChooser.from(this)
                .file(new File(file.getAbsolutePath()))
                .excluded(excluded)
                .requestCode(REQUEST_CODE_OPEN_FILE)
                .authority(BuildConfig.APPLICATION_ID + ".fileprovider")
                .load();

        /*File file = new File(fileInfo.getAbsolutePath());
        String mimeType = MimeType.getMimeType(file);
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (file.getName().endsWith(".apk")) {
                intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
            } else {
                intent.setAction(Intent.ACTION_VIEW);
            }
            Uri uri = FileProvider.getUriForFile(this, "io.zhuliang.appchooser.sample.fileprovider", file);
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mimeType);
        }
        startActivity(intent);*/
    }

    private void showDirectory(FileInfo fileInfo, @Operation int operation) {
        Logger.d(TAG, "showDirectory: " + operation);
        switch (operation) {
            case OPERATION_NONE:
                showDirectoryWithNone();
                break;
            case OPERATION_BACK_PRESSED:
                showDirectoryWithBackPressed();
                break;
            case OPERATION_SELECTED_TAB:
                showDirectoryWithSelectedTab(fileInfo);
                break;
            case OPERATION_CLICK_ITEM:
                showDirectoryWithClickItem(fileInfo);
                break;
            default:
                throw new IllegalArgumentException(operation + " is invalid");
        }
    }

    private void showDirectoryWithNone() {
        Logger.d(TAG, "showDirectoryWithNone11111: " + (mSelectedDirectory == null ? "null" : mSelectedDirectory.getName()) + ", size: " + mDirectories.size());
        if (mSelectedDirectory == null) {
            mSelectedDirectory = new FileInfo(Environment.getExternalStorageDirectory());
            mDirectories.add(mSelectedDirectory);
        }

        mTabLayout.removeAllTabs();
        final int size = mDirectories.size();
        for (int i = 0; i < size; i++) {
            FileInfo directory = mDirectories.get(i);
            if (i == size - 1) {
                mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.directory_tab_view_without_arrow)
                        .setText(directory.getName()).setTag(directory), false);
            } else {
                mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.directory_tab_view)
                        .setText(directory.getName()).setTag(directory), false);
            }
        }

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        for (FileInfo directory : mDirectories) {
            Fragment fragment = mFragmentManager.findFragmentByTag(directory.getAbsolutePath());
            if (fragment != null && !fragment.isDetached()) {
                if (!mSelectedDirectory.equals(directory)) {
                    ft.detach(fragment);
                }
            }
        }
        Fragment selected = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
        if (selected == null) {
            selected = FileInfosFragment.newInstance(mSelectedDirectory.getAbsolutePath());
            ft.add(R.id.contentFrame, selected, mSelectedDirectory.getAbsolutePath());
        } else {
            ft.attach(selected);
        }
        ft.commit();

        Message msg = mMyHandler.obtainMessage();
        msg.arg1 = mDirectories.indexOf(mSelectedDirectory);
        Logger.d(TAG, "showDirectoryWithNone22222: " + (mSelectedDirectory == null ? "null" : mSelectedDirectory.getName()) + ", size: " + mDirectories.size());
        mMyHandler.sendMessageDelayed(msg, 100L);
    }

    private void showDirectoryWithBackPressed() {
        Preconditions.checkNotNull(mSelectedDirectory, "mSelectedDirectory == null");
        Preconditions.checkArgument(mDirectories.contains(mSelectedDirectory),
                "mDirectories not contain:" + mSelectedDirectory.getAbsolutePath());
        final int selectedPosition = mDirectories.indexOf(mSelectedDirectory);
        if (selectedPosition == 0) {
            finish();
        } else {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
            if (f != null && !f.isDetached()) {
                ft.detach(f);
            }
            int previousPosition = selectedPosition - 1;
            FileInfo previous = mSelectedDirectory = mDirectories.get(previousPosition);
            f = mFragmentManager.findFragmentByTag(previous.getAbsolutePath());
            if (f == null) {
                f = FileInfosFragment.newInstance(previous.getAbsolutePath());
                ft.add(R.id.contentFrame, f, previous.getAbsolutePath());
            } else {
                ft.attach(f);
            }
            ft.commit();

            Message msg = mMyHandler.obtainMessage();
            msg.arg1 = previousPosition;
            mMyHandler.sendMessageDelayed(msg, 100L);
        }
    }

    private void showDirectoryWithSelectedTab(FileInfo fileInfo) {
        Preconditions.checkNotNull(fileInfo, "fileInfo == null");
        Preconditions.checkNotNull(mSelectedDirectory, "mSelectedDirectory == null");
        Logger.d(TAG, "showDirectoryWithSelectedTab: " + fileInfo.getName());
        if (mSelectedDirectory != fileInfo) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
            if (f != null && !f.isDetached()) {
                ft.detach(f);
            }

            mSelectedDirectory = fileInfo;

            f = mFragmentManager.findFragmentByTag(fileInfo.getAbsolutePath());
            if (f == null) {
                f = FileInfosFragment.newInstance(fileInfo.getAbsolutePath());
                ft.add(R.id.contentFrame, f, fileInfo.getAbsolutePath());
            } else {
                ft.attach(f);
            }
            ft.commit();
        }
    }

    private void showDirectoryWithClickItem(@NonNull FileInfo fileInfo) {
        Preconditions.checkNotNull(fileInfo, "fileInfo == null");
        // 如果用户当前选中的文件夹已经添加到 mDirectories 中
        String selectedPath = fileInfo.getAbsolutePath();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Iterator<FileInfo> iterator = mDirectories.iterator();
        while (iterator.hasNext()) {
            FileInfo child = iterator.next();
            String childPath = child.getAbsolutePath();
            if (!selectedPath.contains(childPath)) {
                Fragment f = mFragmentManager.findFragmentByTag(childPath);
                if (f != null) {
                    ft.remove(f);
                }
                iterator.remove();
                int tabPosition = getPositionForTab(child);
                if (tabPosition != -1) {
                    mTabLayout.removeTabAt(tabPosition);
                }
            }
        }
        if (mDirectories.contains(mSelectedDirectory)) {
            Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
            if (f != null && !f.isDetached()) {
                ft.detach(f);
            }
        } else {
            Fragment f = mFragmentManager.findFragmentByTag(mSelectedDirectory.getAbsolutePath());
            if (f != null) {
                ft.remove(f);
            }
        }

        if (!mDirectories.contains(fileInfo)) {
            int insertedPosition = mDirectories.size();
            if (insertedPosition > 0) {
                int lastPosition = insertedPosition - 1;
                TabLayout.Tab lastTab = mTabLayout.getTabAt(lastPosition);
                if (lastTab == null) {
                    throw new NullPointerException("lastTab == null");
                }
                lastTab.setCustomView(null);
                lastTab.setCustomView(R.layout.directory_tab_view);
            }
            mDirectories.add(insertedPosition, fileInfo);
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.directory_tab_view_without_arrow)
                    .setText(fileInfo.getName()).setTag(fileInfo), insertedPosition);
        } else {
            int position = mDirectories.indexOf(fileInfo);
            if (position != mDirectories.size() - 1) {
                throw new IllegalStateException(position + " is not last one");
            }
            TabLayout.Tab lastTab = mTabLayout.getTabAt(position);
            if (lastTab == null) {
                throw new NullPointerException("lastTab == null");
            }
            lastTab.setCustomView(null);
            lastTab.setCustomView(R.layout.directory_tab_view_without_arrow);
        }

        Fragment f = mFragmentManager.findFragmentByTag(fileInfo.getAbsolutePath());
        if (f == null) {
            f = FileInfosFragment.newInstance(fileInfo.getAbsolutePath());
            ft.add(R.id.contentFrame, f, fileInfo.getAbsolutePath());
        } else {
            ft.attach(f);
        }
        mSelectedDirectory = fileInfo;
        ft.commit();

        Message msg = mMyHandler.obtainMessage();
        msg.arg1 = mDirectories.indexOf(fileInfo);
        mMyHandler.sendMessageDelayed(msg, 100L);
    }

    private int getPositionForTab(FileInfo directory) {
        int tabCount = mTabLayout.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TabLayout.Tab tab = Preconditions.checkNotNull(mTabLayout.getTabAt(i));
            FileInfo tag = getTabTag(tab);
            if (tag.equals(directory)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    private FileInfo getTabTag(@NonNull TabLayout.Tab tab) {
        FileInfo tabTag = (FileInfo) tab.getTag();
        if (tabTag == null) {
            throw new NullPointerException("tabTag == null");
        }
        return tabTag;
    }

    private void selectTab(int position) {
        if (position < 0) {
            return;
        }
        TabLayout.Tab tab = position < mTabLayout.getTabCount()
                ? mTabLayout.getTabAt(position)
                : null;
        if (tab == null) {
            return;
        }
        tab.select();
        /*try {
            Method method = TabLayout.class.getDeclaredMethod("selectTab", TabLayout.Tab.class);
            method.setAccessible(true);
            method.invoke(mTabLayout, tab);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private static class MyHandler extends Handler {

        private WeakReference<FileInfosActivity> mReference;

        private MyHandler(FileInfosActivity fileInfosActivity) {
            mReference = new WeakReference<>(fileInfosActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FileInfosActivity fileInfosActivity = mReference.get();
            if (fileInfosActivity != null) {
                int selectedTabPosition = msg.arg1;
                fileInfosActivity.selectTab(selectedTabPosition);
            }
        }
    }
}
