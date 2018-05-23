package io.zhuliang.appchooser.sample.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.julian.appchooser.sample.R;
import io.zhuliang.appchooser.sample.module.fileinfos.FileInfosActivity;
import io.zhuliang.appchooser.sample.module.share.ShareActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onActionViewClick(View view) {
        FileInfosActivity.start(this);
    }

    public void onActionSendClick(View view) {
        ShareActivity.start(this);
    }
}
