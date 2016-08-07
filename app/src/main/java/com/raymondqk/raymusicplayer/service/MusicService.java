package com.raymondqk.raymusicplayer.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.raymondqk.raymusicplayer.R;
import com.raymondqk.raymusicplayer.widget.MusicWidgetProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by 陈其康 raymondchan on 2016/8/4 0004.
 */
public class MusicService extends Service {

    public static final int MODE_LOOP_ALL = 0;
    public static final int MODE_LOOP_ONE = 1;
    public static final int MODE_RADOM = 2;

    public static final int STATE_PLAYING = 0;
    //    public static final int STATE_PAUSE = 1;
    public static final int STATE_STOP = 2;

    public static final int MSG_STOP = 0;
    public static final int MSG_PLAY = 1;
    public static final int MSG_PAUSE = 2;
    public static final int MSG_NEXT = 3;
    public static final int MSG_PREVIEW = 4;
    private MusicServiceReceiver mMusicServiceReceiver;

    public boolean isFavor() {
        return isFavor;
    }

    public void setFavor(boolean favor) {
        isFavor = favor;
    }

    private boolean isFavor = false;
    private int play_mode = MODE_LOOP_ALL;
    private int play_state = STATE_STOP;

    public boolean isFisrtPlay() {
        return fisrtPlay;
    }

    private boolean fisrtPlay = true;

    public int getCurrent_Avatar() {
        return current_Avatar;
    }

    private int current_Avatar = 0;
    private int current_duration;
    private int current_pisition;


    MusicServiceBinder mBinder = new MusicServiceBinder();
    private MediaPlayer mMediaPlayer;

    private ArrayList<Integer> mAvatarResIdList = new ArrayList<Integer>();
    private int[] mAvatars = {R.drawable.avatar_joyce, R.drawable.avatar_bigbang};
    private ArrayList<Uri> mMusicUriList = new ArrayList<Uri>();

    public int getCurrentIndex() {
        return currentIndex;
    }

    private int currentIndex;

    public ArrayList<String> getTitleList() {
        return mTitleList;
    }

    public ArrayList<Integer> getAvatarResIdList() {
        return mAvatarResIdList;
    }

    public ArrayList<String> getArtistList() {
        return mArtistList;
    }

    private ArrayList<String> mTitleList = new ArrayList<String>();
    private ArrayList<String> mArtistList = new ArrayList<String>();

    public void setSeekTo(float percent) {
        mMediaPlayer.seekTo((int) (current_duration * percent));
    }

    public void onListItemClick(int position) {
        if (isFisrtPlay()) {
            fisrtPlay = false;
        }
        currentIndex = position;
        current_Avatar = mAvatarResIdList.get(currentIndex % mAvatarResIdList.size());
        play_state = STATE_PLAYING;
        playMusic();
    }

    public void removePlayCallback(PlayCallback playCallback) {
        mPlayCallbackHashSet.remove(playCallback);
    }

    public void stopMediaPlayer() {
        mMediaPlayer.pause();
    }

    public void continueMediaPlayer() {
        mMediaPlayer.start();
    }


    public interface OnCompletionCallback {
        void OnCompletion();
    }

    public interface PlayCallback {
        void onPlayPrepared();
    }


    private OnCompletionCallback mCompletionCallback;

    public void setCompletionCallback(OnCompletionCallback completionCallback) {
        mCompletionCallback = completionCallback;
    }


    private HashSet<PlayCallback> mPlayCallbackHashSet = new HashSet<PlayCallback>();
    //    private PlayCallback mPlayCallback;

    public void setPlayCallback(PlayCallback playCallback) {
        mPlayCallbackHashSet.add(playCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //记得释放资源
        mMediaPlayer.stop();
        mMediaPlayer.release();
        unregisterReceiver(mMusicServiceReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Test", "onStartCommand");
        return START_NOT_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化音乐文件列表
        initMusicFiles();
        //初始化MediaPlayer
        initMediaPlayer();
        //注册BroadcastReceiver，这是用来接收来自Widget的广播的
        registBroadcastReceiverForWidget();
    }

    /**
     * 注册BroadcastReceiver，这是用来接收来自Widget的广播的
     */
    private void registBroadcastReceiverForWidget() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicWidgetProvider.WIDGET_PLAY);
        intentFilter.addAction(MusicWidgetProvider.WIDGET_NEXT);
        intentFilter.addAction(MusicWidgetProvider.WIDGET_PREVIEW);
        mMusicServiceReceiver = new MusicServiceReceiver();
        registerReceiver(mMusicServiceReceiver, intentFilter);
        Log.i("Test", "registReceiver");
    }

    /**
     * 初始化MediaPlayer
     */
    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        //设置播放结束的监听事件
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //判断当前播放模式是否为单曲循环
                if (play_mode != MODE_LOOP_ONE) {
                    //如果非单曲循环，则进行播放下一首的操作
                    mCompletionCallback.OnCompletion();//这是一个回调，让Activity更新UI等操作
                } else {
                    //若为单曲循环，则直接开始播放，不需要重新setDataSource
                    mMediaPlayer.start();
                }
            }
        });
    }

    /**
     * 准备音乐文件
     * 这里面日后学习了扫描sdcard和获取系统媒体资源之后，重新设计
     * 目前是使用raw里面的文件
     */
    private void initMusicFiles() {
        //android 获取raw 绝对路径 -- raw资源转uri
        for (int i = 0; i < 10; i++) {     // 通过循环重复加载uri到list里面，模拟有多首歌曲的情况
            //将raw资源转化为uri
            Uri uri = Uri.parse("android.resource://com.raymondqk.raymusicplayer/" + R.raw.missyou);
            //加入到musicList
            mMusicUriList.add(uri);
            //把头像资源加入到头像的list里面 与music同步加入，根据index就可以将music和头像对应起来，这是目前的暂缓之策，日后应当根据music的title找到对应的头像图片
            mAvatarResIdList.add(R.drawable.avatar_joyce);

            // TODO: 2016/8/4 0004 因为MediaPlayer似乎无法读取文件里面的歌曲信息，如标题和艺术家，所以目前这样处理着
            mTitleList.add("好想你");
            mArtistList.add("Joyce");

            //这是第二首
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stillalive);
            mMusicUriList.add(uri);
            mAvatarResIdList.add(R.drawable.avatar_bigbang);
            mTitleList.add("STILL ALIVE");
            mArtistList.add("BIGBANG");
        }
    }


    /**
     * 设置当前播放模式：单曲、循环
     *
     * @param play_mode 循环模式 : 可选 MODE_LOOP_ALL/MODE_LOOP_ONE
     */
    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    /**
     * 设置当前播放状态：播放中、暂停,同时进行播放操作，是播放还是暂停，暂时未添加停止项
     * 外部只需要通知播放暂停按键被按下了即可，剩下操作留给service来判断
     */
    public int setPlay_state() {

        /*
        当前是播放状态，则进行暂停操作
         */

        if (play_state == STATE_PLAYING) {
            play_state = STATE_STOP;
            mMediaPlayer.pause();
        } else if (play_state == STATE_STOP) {
            play_state = STATE_PLAYING;
            if (fisrtPlay) {
                 /*
                因为刚打开播放器，未添加音乐文件给MediaPlayer，所以做这么一个判断，以防出bug
                若第一次播放，执行播放函数playMusic()，设置setDataSource等操作
                 */
                fisrtPlay = false;
                playMusic();
            } else {
                 /*
                若不是第一次播放，则代表是播放过程中暂停了，现在继续即可。
                 */
                mMediaPlayer.start();
            }
        }
        return play_state;
    }


    public int getPlay_mode() {

        return play_mode;
    }

    public int getPlay_state() {
        return play_state;
    }

    public String title() {
        return mTitleList.get(currentIndex % mTitleList.size());
    }

    public String artist() {
        return mArtistList.get(currentIndex % mArtistList.size());
    }

    public void nextMusic() {
        // TODO: 2016/8/4 0004 播放下一首
        //        Toast.makeText(MusicService.this, "下一首", Toast.LENGTH_SHORT).show();
        //        if (currentIndex > 0) {
        //            currentIndex++;
        //        }
        if (fisrtPlay) {

        } else {
            currentIndex++;
            current_Avatar = mAvatarResIdList.get(currentIndex % mAvatarResIdList.size());

            playMusic();
        }

    }

    public void playMusic() {
        Log.i("Test", "play");
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(MusicService.this, mMusicUriList.get(currentIndex % mMusicUriList.size()));
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Test", "无法播放音乐");
            mMediaPlayer.reset();
        }
        beforePlay();

        mMediaPlayer.start();
    }


    private void beforePlay() {
        current_duration = mMediaPlayer.getDuration();
        //        mPlayCallback.onPlayPrepared();
        for (PlayCallback playCallback : mPlayCallbackHashSet) {
            playCallback.onPlayPrepared();
        }

    }

    public void previewMusic() {
        // TODO: 2016/8/4 0004
        //        Toast.makeText(MusicService.this, "上一首", Toast.LENGTH_SHORT).show();
        if (fisrtPlay) {

        } else {
            if (currentIndex > 0) {
                currentIndex--;
            } else {
                //实现列表前一首到头时，直接跳到队尾。
                currentIndex = mMusicUriList.size() - 1;
            }

            current_Avatar = mAvatarResIdList.get(currentIndex % mAvatarResIdList.size());
            playMusic();
        }
    }

    public String getCurrent_duration() {
        return getTimeStrByMils(current_duration);

    }


    private String getTimeStrByMils(int mils) {
        int seconds = mils / 1000;
        int min = seconds / 60;
        int sec = seconds % 60;
        String min_str;
        String sec_str;
        if (min < 10) {
            min_str = "0" + min;
        } else {
            min_str = min + "";
        }
        if (sec < 10) {
            sec_str = "0" + sec;
        } else {
            sec_str = sec + "";
        }
        return min_str + ":" + sec_str;
    }

    public void setCurrent_duration(int current_duration) {
        this.current_duration = current_duration;
    }

    public String getCurrent_pisition() {
        return getTimeStrByMils(mMediaPlayer.getCurrentPosition());
    }

    public float getProgressPercent() {

        return (float) mMediaPlayer.getCurrentPosition() / (float) mMediaPlayer.getDuration();
    }

    public class MusicServiceBinder extends Binder {
        public MusicService getServiceInstance() {
            return MusicService.this;
        }
    }

    class MusicServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {
                if (TextUtils.equals(intent.getAction(), MusicWidgetProvider.WIDGET_PLAY)) {
                    setPlay_state();
                    Log.i("TEST", "service-onReceive-PLAY");
                } else if (TextUtils.equals(intent.getAction(), MusicWidgetProvider.WIDGET_NEXT)) {
                    nextMusic();
                    Log.i("TEST", "service-onReceive-next");
                } else if (TextUtils.equals(intent.getAction(), MusicWidgetProvider.WIDGET_PREVIEW)) {
                    previewMusic();
                    Log.i("TEST", "service-onReceive-PRE");
                }
            }

        }
    }
}

