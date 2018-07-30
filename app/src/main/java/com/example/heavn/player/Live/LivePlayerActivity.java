package com.example.heavn.player.Live;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.player.Music.MusicActivity;
import com.example.heavn.player.Music.MusicListActivity;
import com.example.heavn.player.Music.MusicPlayerActivity;
import com.example.heavn.player.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Administrator on 2018/3/28 0028.
 */

public class LivePlayerActivity extends AppCompatActivity implements View.OnClickListener{
    private VideoView videoView;
    private static int RETRY_TIMES = 5;
    private int count = 0;
    private ImageView back;
    private TextView system_time;
    private TextView live;
    private ImageView play_pause;
    private Intent intent;
    private String live_name;
    private String live_path;
    private RelativeLayout rootView;
    private RelativeLayout loadingView;
    private LinearLayout topView;
    private LinearLayout bottomView;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_player_layout);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        intent = getIntent();
        live_path = intent.getStringExtra("live_path");
        live_name = intent.getStringExtra("live_name");

        initView();

        initPlayer();

        if(videoView.isPlaying()){
            play_pause.setImageResource(R.drawable.pause);
        }else{
            play_pause.setImageResource(R.drawable.play);
        }
    }

    private void initView(){
        rootView = (RelativeLayout)findViewById(R.id.activty_layout);
        rootView.setOnClickListener(this);
        topView = (LinearLayout)findViewById(R.id.top_layout);
        bottomView = (LinearLayout)findViewById(R.id.bottom_layout);
        loadingView = (RelativeLayout)findViewById(R.id.relativelayout);

        back = (ImageView)topView.findViewById(R.id.live_back);
        back.setOnClickListener(this);
        system_time = (TextView)topView.findViewById(R.id.sys_time);
        system_time.setText(formatSystemTime(System.currentTimeMillis()));
        live = (TextView)topView.findViewById(R.id.live_name);
        live.setText(live_name);
        play_pause = (ImageView)bottomView.findViewById(R.id.imageView_play);
        play_pause.setOnClickListener(this);
    }

    private String formatSystemTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(new Date(time));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.activty_layout:
                if(topView.getVisibility() == View.VISIBLE || bottomView.getVisibility() == View.VISIBLE){
                    topView.setVisibility(View.GONE);
                    bottomView.setVisibility(View.GONE);
                }else{
                    topView.setVisibility(View.VISIBLE);
                    bottomView.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            topView.setVisibility(View.GONE);
                            bottomView.setVisibility(View.GONE);
                        }
                    },3000);
                }
                if(videoView.isPlaying()){
                    play_pause.setImageResource(R.drawable.pause);
                }else{
                    play_pause.setImageResource(R.drawable.play);
                }
                break;
            case R.id.live_back:
                finish();
                break;
            case R.id.imageView_play:
                if(videoView.isPlaying()){
                    videoView.stopPlayback();
                    play_pause.setImageResource(R.drawable.play);
                }else{
                    videoView.setVideoURI(Uri.parse(live_path));
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            videoView.start();
                        }
                    });
                    play_pause.setImageResource(R.drawable.pause);
                }
                break;
            default:
                break;
        }


    }

    private void initPlayer(){
        Vitamio.isInitialized(getApplicationContext());
        videoView = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse(live_path);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if(count >RETRY_TIMES){
                    new AlertDialog.Builder(LivePlayerActivity.this)
                            .setMessage("Error")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false).show();
                }else{
                    videoView.stopPlayback();
                    videoView.setVideoURI(Uri.parse(live_path));
                }
                count ++;
                return false;
            }
        });
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        loadingView.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        loadingView.setVisibility(View.GONE);
                        break;

                }
                return false;
            }
        });

    }
}
