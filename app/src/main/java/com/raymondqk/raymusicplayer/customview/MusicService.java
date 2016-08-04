package com.raymondqk.raymusicplayer.customview;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.raymondqk.raymusicplayer.R;

import java.io.IOException;
import java.util.ArrayList;

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
    private int currentIndex;

    public void setSeekTo(float percent) {
        mMediaPlayer.seekTo((int) (current_duration * percent));
    }

    public interface SetAvatarCallBack {
        void setAvatar(int resId);
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


    private PlayCallback mPlayCallback;

    public void setPlayCallback(PlayCallback playCallback) {
        mPlayCallback = playCallback;
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //android 获取raw 绝对路径 -- raw资源转uri
        Uri uri = Uri.parse("android.resource://com.raymondqk.raymusicplayer/" + R.raw.missyou);
        mMusicUriList.add(uri);
        mAvatarResIdList.add(R.drawable.avatar_joyce);

        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stillalive);
        mMusicUriList.add(uri);
        mAvatarResIdList.add(R.drawable.avatar_bigbang);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (play_mode != MODE_LOOP_ONE) {
                    mCompletionCallback.OnCompletion();
                }else {

                    mMediaPlayer.start();
                }
            }
        });
    }


    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public void setPlay_state(int play_state) {
        this.play_state = play_state;
        if (this.play_state == STATE_PLAYING) {
            if (fisrtPlay) {
                playMusic();
                fisrtPlay = false;
            } else {
                continueMusic();
            }

        } else if (this.play_state == STATE_STOP) {
            pauseMusic();
        }
    }

    public void continueMusic() {
        mMediaPlayer.start();
    }

    private void pauseMusic() {
        mMediaPlayer.pause();

    }

    public int getPlay_mode() {

        return play_mode;
    }

    public int getPlay_state() {
        return play_state;
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
            mMediaPlayer.reset();
            playMusic();
        }

    }

    public void playMusic() {


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
        mPlayCallback.onPlayPrepared();
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
            //        mSetAvatarCallBack.setAvatar(mAvatars[currentIndex % mAvatars.length]);
            current_Avatar = mAvatarResIdList.get(currentIndex % mAvatarResIdList.size());
            mMediaPlayer.reset();
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


}

