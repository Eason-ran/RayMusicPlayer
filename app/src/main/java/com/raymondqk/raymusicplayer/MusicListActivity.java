package com.raymondqk.raymusicplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by 陈其康 raymondchan on 2016/8/3 0003.
 */
public class MusicListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist);
        initView();
    }

    private void initView() {
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle("歌曲列表");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_search:
                Toast.makeText(MusicListActivity.this, "search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_item_setting:
                Toast.makeText(MusicListActivity.this, "setting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_item_share:
                Toast.makeText(MusicListActivity.this, "share", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
