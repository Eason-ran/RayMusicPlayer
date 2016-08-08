package com.raymondqk.raymusicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 陈其康 raymondchan on 2016/8/8 0008.
 */
public class MySharedPreference {
    public static final String RAY_MUSIC_PLAYER = "RayMusicPlayer";
    //    public static boolean isFirstLaunch = true;
    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private SharedPreferences.Editor mEditor;

    public MySharedPreference(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(RAY_MUSIC_PLAYER, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void setData(String key, String value) {
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public void setData(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.apply();
    }
    public void setData(String key,boolean value){
        mEditor.putBoolean(key,value);
        mEditor.apply();
    }

    public boolean getData(String key){
        return mSharedPreferences.getBoolean(key,true);
    }

}
