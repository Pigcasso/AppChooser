package io.zhuliang.appchooser.sample.module.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import io.zhuliang.appchooser.AppChooser;
import io.zhuliang.appchooser.sample.R;

public class ShareActivity extends AppCompatActivity {

    private ComponentName[] mComponentNames = new ComponentName[]{
            new ComponentName("nutstore.android", "nutstore.android.SendToNutstoreIndex"),
            new ComponentName("nutstore.android.debug", "nutstore.android.SendToNutstoreIndex")
    };

    public static void start(Context context) {
        Intent starter = new Intent(context, ShareActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void onShareClick(View view) {
        EditText editText = findViewById(R.id.edit_text);
        if (editText != null) {
            CharSequence shareContent = editText.getText();
            AppChooser.from(this).text(shareContent.toString()).excluded(mComponentNames).load();
        }
    }
}
