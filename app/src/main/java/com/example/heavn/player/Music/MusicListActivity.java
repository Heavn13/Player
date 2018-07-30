package com.example.heavn.player.Music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.heavn.player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class MusicListActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton back;
    private ImageButton search;
    private EditText edit;
    private TextView songName;
    private TextView singer;
    private ImageButton play_pause;
    private String name;
    private ListView listView;
    private List<Song> songList;
    private MusicAdapter adapter;
    private MusicListActivityReceiver musicListActivityReceiver;
    //音乐播放状态
    int status = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list_layout);

        back = (ImageButton)findViewById(R.id.back);
        back.setOnClickListener(this);

        search = (ImageButton)findViewById(R.id.search);
        search.setOnClickListener(this);

        edit = (EditText)findViewById(R.id.edit);
        edit.setOnClickListener(this);

        songName = (TextView)findViewById(R.id.song_name);
        songName.setOnClickListener(this);
        singer = (TextView)findViewById(R.id.singer);
        singer.setOnClickListener(this);

        play_pause = (ImageButton)findViewById(R.id.play_pause);
        play_pause.setOnClickListener(this);

        initView();
        //动态注册该类的广播
        musicListActivityReceiver = new MusicListActivityReceiver();
        //创建IntentFilter
        IntentFilter filter = new IntentFilter();
        //指定BroadcastReceiver监听的Action
        filter.addAction(MusicActivity.UPDATEUI_ACTION);
        //注册BroadcastReceiver
        registerReceiver(musicListActivityReceiver, filter);

        //初始化也要发送广播，否则创建activity时无法第一时间刷新
        Intent intent = new Intent(MusicActivity.CONTROL_ACTION);
        //发送广播，将被Service中的BroadcastReceiver接收到
        sendBroadcast(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.search:
                if(edit.getVisibility() == View.INVISIBLE) edit.setVisibility(View.VISIBLE);
                else name = edit.getText().toString();
                for(int i = 0;i < songList.size();i++){
                    if(songList.get(i).song.equals(name)) {
                        listView.setSelection(i);
                        listView.setFocusable(true);
                    }
                }
                break;
            case R.id.play_pause:
                intent = new Intent(MusicActivity.CONTROL_ACTION);
                intent.putExtra("control", MusicActivity.PLAY);
                //发送广播，将被Service中的BroadcastReceiver接收到
                sendBroadcast(intent);
                break;
            case R.id.song_name:
                intent = new Intent(MusicListActivity.this,MusicPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.singer:
                intent = new Intent(MusicListActivity.this,MusicPlayerActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }


    }

    private void initView() {
        listView = (ListView) findViewById(R.id.music_list);
        songList = new ArrayList<>();
        //把扫描到的音乐赋值给list
        songList = MusicUtils.getMusicData(this);
        adapter = new MusicAdapter(MusicListActivity.this, songList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MusicActivity.CONTROL_ACTION);
                intent.putExtra("control", MusicActivity.START);
                intent.putExtra("currentSong",position);
                //发送广播，将被Service中的BroadcastReceiver接收到
                sendBroadcast(intent);
            }
        });
    }

    public class MusicListActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取Intent中的update消息，update代表播放状态
            int updateUI = intent.getIntExtra("updateUI", -1);
            //获取Intent中的current消息，current代表当前正在播放的歌曲
            int currentSong = intent.getIntExtra("currentSong", -1);
            if (currentSong >= 0){
                songName.setText(songList.get(currentSong).song);
                singer.setText(songList.get(currentSong).singer);
            }
            switch (updateUI){
                case MusicActivity.NOPLAY:
                    play_pause.setBackgroundResource(R.drawable.play);
                    status = MusicActivity.ISPLAY;
                    break;
                //控制系统进入播放状态
                case MusicActivity.ISPLAY:
                    //在播放状态下设置使用暂停图标
                    play_pause.setBackgroundResource(R.drawable.pause);
                    status = MusicActivity.ISPAUSE;
                    break;
                case MusicActivity.ISPAUSE:
                    //在暂停状态下设置使用播放图标
                    play_pause.setBackgroundResource(R.drawable.play);
                    status = MusicActivity.ISPLAY;
                    break;
            }
        }
    }

}

