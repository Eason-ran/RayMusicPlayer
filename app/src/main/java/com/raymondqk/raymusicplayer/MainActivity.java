package com.raymondqk.raymusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.raymondqk.raymusicplayer.customview.AvatarCircle;

/**
 * Created by 陈其康 raymondchan on 2016/8/3 0003.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    AvatarCircle mAvatarCircle;
    private boolean isClickAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        getSupportActionBar().setSubtitle("播放界面");
        mAvatarCircle = (AvatarCircle) findViewById(R.id.avatar_main);
        mAvatarCircle.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_list:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MusicListActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.avatar_main:
        if (isClickAvatar) {
            mAvatarCircle.setImageResource(R.drawable.avatar_joyce2);
            isClickAvatar = !isClickAvatar;
        }else {
            mAvatarCircle.setImageResource(R.drawable.avatar_joyce);
            isClickAvatar = !isClickAvatar;
        }
}
    }
}
