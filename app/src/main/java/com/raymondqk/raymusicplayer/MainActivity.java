package com.raymondqk.raymusicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.raymondqk.raymusicplayer.customview.AvatarCircle;
import com.raymondqk.raymusicplayer.customview.MusicService;

/**
 * Created by 陈其康 raymondchan on 2016/8/3 0003.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, MusicService.SetAvatarCallBack {


    private AvatarCircle mAvatarCircle;
    private boolean isClickAvatar;
    private ImageButton mIb_play_mode;
    private ImageButton mIb_play;
    private ImageButton mIb_preview;
    private ImageButton mIb_next;

    private MusicService mMusicService;


    //    MusicService.SetAvatarCallBack mSetAvatarCallBack = new MusicService.SetAvatarCallBack() {
    //        @Override
    //        public void setAvatar(int resId) {
    //            mAvatarCircle.setImageResource(resId);
    //        }
    //    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicServiceBinder binder = (MusicService.MusicServiceBinder) service;
            mMusicService = binder.getServiceInstance();
            if (mMusicService != null) {
                Toast.makeText(MainActivity.this, "音乐服务绑定成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "音乐服务绑定失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicService = null;

        }
    };
    private Intent mMusicSeviceIntent;

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
        mIb_play_mode = (ImageButton) findViewById(R.id.ib_play_mode);
        mIb_play = (ImageButton) findViewById(R.id.ib_play);
        mIb_preview = (ImageButton) findViewById(R.id.ib_preview);
        mIb_next = (ImageButton) findViewById(R.id.ib_next);

        mIb_next.setOnClickListener(this);
        mIb_play.setOnClickListener(this);
        mIb_play_mode.setOnClickListener(this);
        mIb_preview.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        serviceInit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMusicService = null;
        unbindService(mServiceConnection);
        stopService(mMusicSeviceIntent);
        Log.i("Test", "MainActivity onDestroy");
    }

    private void serviceInit() {
        mMusicSeviceIntent = new Intent();
        mMusicSeviceIntent.setClass(MainActivity.this, MusicService.class);
        bindService(mMusicSeviceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        switch (v.getId()) {
            case R.id.avatar_main:
                if (isClickAvatar) {
                    mAvatarCircle.setImageResource(R.drawable.avatar_joyce2);
                    isClickAvatar = !isClickAvatar;
                } else {
                    mAvatarCircle.setImageResource(R.drawable.avatar_joyce);
                    isClickAvatar = !isClickAvatar;
                }
                break;
            case R.id.ib_play_mode:
                if (mMusicService != null) {
                    if (mMusicService.getPlay_mode() == MusicService.MODE_LOOP_ALL) {
                        mMusicService.setPlay_mode(MusicService.MODE_LOOP_ONE);
                        mIb_play_mode.setImageResource(R.drawable.loop_one);
                        // TODO: 2016/8/4 0004 设置播放模式

                    } else if (mMusicService.getPlay_mode() == MusicService.MODE_LOOP_ONE) {
                        mMusicService.setPlay_mode(MusicService.MODE_RADOM);
                        mIb_play_mode.setImageResource(R.drawable.radom);
                        // TODO: 2016/8/4 0004 设置播放模式
                    } else if (mMusicService.getPlay_mode() == MusicService.MODE_RADOM) {
                        mMusicService.setPlay_mode(MusicService.MODE_LOOP_ALL);
                        mIb_play_mode.setImageResource(R.drawable.loop_all);
                    }
                }
                break;
            case R.id.ib_play:
                if (mMusicService != null) {
                    if (mMusicService.getPlay_state() == MusicService.STATE_STOP) {
                        mIb_play.setImageResource(R.drawable.pause);
                        mMusicService.setPlay_state(MusicService.STATE_PLAYING);
                        // TODO: 2016/8/4 0004 在Service里面进行音乐播放的操作
                    } else {
                        mIb_play.setImageResource(R.drawable.play);
                        mMusicService.setPlay_state(MusicService.STATE_STOP);
                        // TODO: 2016/8/4 0004 在Service里面进行音乐暂停的操作
                    }
                }
                break;
            case R.id.ib_next:
                if (!mMusicService.isFisrtPlay()) {
                    mMusicService.nextMusic();
                    mAvatarCircle.setImageResource(mMusicService.getCurrent_Avatar());
                }
                break;
            case R.id.ib_preview:
                if (!mMusicService.isFisrtPlay()) {
                    mMusicService.previewMusic();
                    mAvatarCircle.setImageResource(mMusicService.getCurrent_Avatar());

                }

                break;
        }
    }

    @Override
    public void setAvatar(int resId) {
        mAvatarCircle.setImageResource(resId);
    }
}
