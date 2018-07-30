package com.example.heavn.player.Music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.heavn.player.Item;
import com.example.heavn.player.ItemAdapter;
import com.example.heavn.player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class MusicActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton back;
    private TextView songName;
    private TextView singer;
    private ImageButton play_pause;
    private List<Item> items = new ArrayList<Item>();
    private List<Song> songList;

    private MusicActivityReceiver musicActivityReceiver;
    public static final String CONTROL_ACTION = "com.example.heavn.player.CONTROL_ACTION";
    public static final String UPDATEUI_ACTION = "com.example.heavn.player.UPDATEUI_ACTION";
    //音乐播放状态
    int status = 10;
    public static final int NOPLAY = 10;
    public static final int ISPLAY = 11;
    public static final int ISPAUSE = 12;
    //音乐操作指令
    int control = -1;
    public static final int PLAY = 1;
    public static final int START = 2;
    public static final int NEXT = 3;
    public static final int LAST = 4;
    public static final int TYPE= 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_activity_layout);

        songList = new ArrayList<>();
        //把扫描到的音乐赋值给list
        songList = MusicUtils.getMusicData(this);

        back = (ImageButton)findViewById(R.id.back);
        back.setOnClickListener(this);

        songName = (TextView)findViewById(R.id.song_name);
        songName.setOnClickListener(this);
        singer = (TextView)findViewById(R.id.singer);
        singer.setOnClickListener(this);

        play_pause = (ImageButton)findViewById(R.id.play_pause);
        play_pause.setOnClickListener(this);

        init();
        ItemAdapter itemAdapter = new ItemAdapter(getApplicationContext(),R.layout.music_main_item_layout,items);
        ListView listView = (ListView)findViewById(R.id.music_list);
        listView.setAdapter(itemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(MusicActivity.this,MusicListActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
        //动态注册该类的广播
        musicActivityReceiver = new MusicActivityReceiver();
        //创建IntentFilter
        IntentFilter filter = new IntentFilter();
        //指定BroadcastReceiver监听的Action
        filter.addAction(UPDATEUI_ACTION);
        //注册BroadcastReceiver
        registerReceiver(musicActivityReceiver, filter);

        //打开service
        Intent intent = new Intent(MusicActivity.this, MusicService.class);
        //启动后台Service
        startService(intent);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.play_pause:
                intent = new Intent(CONTROL_ACTION);
                intent.putExtra("control", PLAY);
                //发送广播，将被Service中的BroadcastReceiver接收到
                sendBroadcast(intent);
                break;
            case R.id.song_name:
                intent = new Intent(MusicActivity.this,MusicPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.singer:
                intent = new Intent(MusicActivity.this,MusicPlayerActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    //初始化
    public void init(){
        Item item;
        item = new Item("本地音乐",R.drawable.music,"("+String.valueOf(songList.size())+")");
        items.add(item);
    }

    public class MusicActivityReceiver extends BroadcastReceiver {
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
                case NOPLAY:
                    play_pause.setBackgroundResource(R.drawable.play);
                    status = ISPLAY;
                    break;
                //控制系统进入播放状态
                case ISPLAY:
                    //在播放状态下设置使用暂停图标
                    play_pause.setBackgroundResource(R.drawable.pause);
                    status = ISPAUSE;
                    break;
                case ISPAUSE:
                    //在暂停状态下设置使用播放图标
                    play_pause.setBackgroundResource(R.drawable.play);
                    status = ISPLAY;
                    break;
            }
        }
    }

}
