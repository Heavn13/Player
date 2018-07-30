package com.example.heavn.player.Music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.heavn.player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{
    private ImageButton back;
    private TextView songName;
    private TextView singer;
    private ImageButton play_pause;
    private ImageButton next;
    private ImageButton last;
    private ImageButton type;
    private ImageButton list;
    private ImageButton close;
    private SeekBar seekBar;
    private AudioManager audioManager;
    private TextView currentTime;
    private TextView totalTime;
    private MusicPlayerActivityReceiver musicPlayerActivityReceiver;
    private List<Song> songList;
    private ListView pop_list;
    private LittleMusicAdapter musicAdapter;
    private PopupWindow popupWindow;
    //音乐播放模式
    int types = 20;
    public static final int TYPE_ONE = 20;
    public static final int TYPE_RANDOM = 21;
    public static final int TYPE_ALL = 22;
    //音乐播放状态
    int status = 10;
    //音乐操作指令
    int control = -1;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play_interface_layout);

        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        songList = new ArrayList<>();
        //把扫描到的音乐赋值给list
        songList = MusicUtils.getMusicData(this);

        songName = (TextView)findViewById(R.id.song_name);
        singer = (TextView)findViewById(R.id.singer);
        back = (ImageButton)findViewById(R.id.back);
        back.setOnClickListener(this);
        play_pause = (ImageButton)findViewById(R.id.play_pause_interface);
        play_pause.setOnClickListener(this);
        next = (ImageButton)findViewById(R.id.next_interface);
        next.setOnClickListener(this);
        last = (ImageButton)findViewById(R.id.previous_interface);
        last.setOnClickListener(this);
        type = (ImageButton)findViewById(R.id.type_interface);
        type.setOnClickListener(this);
        list = (ImageButton)findViewById(R.id.song_list_interface);
        list.setOnClickListener(this);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        currentTime = (TextView)findViewById(R.id.current_time);
        totalTime = (TextView)findViewById(R.id.total_time);

        //动态注册该类的广播
        musicPlayerActivityReceiver = new MusicPlayerActivityReceiver();
        //创建IntentFilter
        IntentFilter filter = new IntentFilter();
        //指定BroadcastReceiver监听的Action
        filter.addAction(MusicActivity.UPDATEUI_ACTION);
        //注册BroadcastReceiver
        registerReceiver(musicPlayerActivityReceiver, filter);

        //初始化也要发送广播，否则创建activity时无法第一时间刷新
        Intent intent = new Intent(MusicActivity.CONTROL_ACTION);
        //发送广播，将被Service中的BroadcastReceiver接收到
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MusicActivity.CONTROL_ACTION);
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.play_pause_interface:
                intent.putExtra("control", MusicActivity.PLAY);
                break;
            case R.id.next_interface:
                intent.putExtra("control", MusicActivity.NEXT);
                break;
            case R.id.previous_interface:
                intent.putExtra("control", MusicActivity.LAST);
                break;
            case R.id.type_interface:
                intent.putExtra("control", MusicActivity.TYPE);
                break;
            case R.id.song_list_interface:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow= null;
                }else{
                    initPopupWindowView();
                }
                break;
            case R.id.close:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow= null;
                }else{
                    initPopupWindowView();
                }
            default:
                break;
        }
        //发送广播，将被Service中的BroadcastReceiver接收到
        sendBroadcast(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //跳转音乐进度
        MusicService.mediaPlayer.seekTo(seekBar.getProgress());
    }

    public class MusicPlayerActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取Intent中的update消息，update代表播放状态
            int updateUI = intent.getIntExtra("updateUI", -1);
            int t = intent.getIntExtra("type", -1);
            //获取Intent中的current消息，current代表当前正在播放的歌曲
            int currentSong = intent.getIntExtra("currentSong", -1);
            if (currentSong >= 0){
                songName.setText(songList.get(currentSong).song);
                singer.setText(songList.get(currentSong).singer);
                seekBar.setMax(songList.get(currentSong).duration);
                totalTime.setText(""+MusicUtils.formatTime(songList.get(currentSong).duration));
                handler.post(updateSeekBar);
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
                default:
                    break;
            }
            switch (t){
                case MusicPlayerActivity.TYPE_ALL:
                    type.setBackgroundResource(R.drawable.type_all);
                    types = MusicPlayerActivity.TYPE_ALL;
                    break;
                case MusicPlayerActivity.TYPE_RANDOM:
                    type.setBackgroundResource(R.drawable.type_random);
                    types = MusicPlayerActivity.TYPE_RANDOM;
                    break;
                case MusicPlayerActivity.TYPE_ONE:
                    type.setBackgroundResource(R.drawable.type_one);
                    types = MusicPlayerActivity.TYPE_ONE;
                    break;
                default:
                    break;
            }
        }
    }

    Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(MusicService.mediaPlayer.getCurrentPosition());
            currentTime.setText(""+MusicUtils.formatTime(MusicService.mediaPlayer.getCurrentPosition()));
            handler.postDelayed(updateSeekBar,1000);
        }
    };

    //弹出窗口初始化
    public void initPopupWindowView() {
        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.music_popupwindow_layout, null, false);
        //获取手机屏幕宽度
        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView,width,height);
        popupWindow.setAnimationStyle(R.style.animTranslate);
        popupWindow.showAsDropDown(list,-list.getWidth(),-list.getHeight());
        // 自定义view添加触摸事件，设置点击其他区域弹窗消失
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow= null;
                }
                return false;
            }
        });

        //下拉菜单按钮
        close = (ImageButton)customView.findViewById(R.id.close);
        close.setOnClickListener(this);
        pop_list = (ListView)customView.findViewById(R.id.pop_list);
        pop_list.setFocusable(true);
        musicAdapter = new LittleMusicAdapter(MusicPlayerActivity.this, songList);
        pop_list.setAdapter(musicAdapter);
        pop_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

}