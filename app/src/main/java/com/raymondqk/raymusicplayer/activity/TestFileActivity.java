package com.raymondqk.raymusicplayer.activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.raymondqk.raymusicplayer.R;
import com.raymondqk.raymusicplayer.utils.MyFileUtil;

import java.io.BufferedWriter;
import java.io.FileInputStream;

/**
 * Created by 陈其康 raymondchan on 2016/8/9 0009.
 */
public class TestFileActivity extends AppCompatActivity {

    public static final String FILENAME = "data";
    private TextView mFileDir;
    private TextView mCache_dir;
    private EditText mEditText;
    private TextView mTv_file_content;

    private BufferedWriter mWriter;
    private FileInputStream mIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        //        File file = new File()
        mFileDir = (TextView) findViewById(R.id.id_file_internal_dir);
        mCache_dir = (TextView) findViewById(R.id.id_file_internal_cache_dir);


        mFileDir.setText(getFilesDir().getAbsolutePath());
        mCache_dir.setText(getCacheDir().getAbsolutePath());

        mEditText = (EditText) findViewById(R.id.edt_file);
        mTv_file_content = (TextView) findViewById(R.id.tv_file_result);
        if (!TextUtils.equals(MyFileUtil.readInternalFile(this, FILENAME), "")) {
            mTv_file_content.setText(MyFileUtil.readInternalFile(this, FILENAME));
        }
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_ENTER:
                        String data = mEditText.getText().toString();
                        if (TextUtils.equals(data, "clr")) {
                            MyFileUtil.wirteInternalFile(TestFileActivity.this, FILENAME, Context.MODE_PRIVATE, "");
                        } else {
                            MyFileUtil.wirteInternalFile(TestFileActivity.this, FILENAME, Context.MODE_APPEND, data);
                        }

                        mTv_file_content.setText(MyFileUtil.readInternalFile(TestFileActivity.this, FILENAME));

                        if (TextUtils.equals(MyFileUtil.readInternalFile(TestFileActivity.this, FILENAME), "")) {
                            PackageManager manager = getPackageManager();
                            ApplicationInfo info = null;
                            try {
                                info = manager.getApplicationInfo(getPackageName(), 0);

                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                            getSupportActionBar().setTitle(manager.getApplicationLabel(info));
                        } else {
                            getSupportActionBar().setTitle(MyFileUtil.readInternalFile(TestFileActivity.this, FILENAME));
                        }

                        mEditText.setText("");
                }
                return true;
            }
        });

    }


}

