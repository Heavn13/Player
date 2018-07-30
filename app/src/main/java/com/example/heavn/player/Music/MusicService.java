package com.example.heavn.player.Music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class MusicService extends Service {

    ServiceReceiver serviceReceiver;
    private List<Song> songList;
    public static MediaPlayer mediaPlayer;
    private int status = MusicActivity.NOPLAY;
    private int types = MusicPlayerActivity.TYPE_ALL;
    int currentSong = 0; // 记录当前正在播放的音乐

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songList = MusicUtils.getMusicData(this);

        //创建服务
        serviceReceiver = new ServiceReceiver();
        //动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicActivity.CONTROL_ACTION);
        registerReceiver(serviceReceiver, filter);
        //创建MediaPlayer
        mediaPlayer = new MediaPlayer();
        //为MediaPlayer播放完成事件绑定监听器
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(types == MusicPlayerActivity.TYPE_ALL ){
                    currentSong ++;
                    if(currentSong > songList.size()){
                        currentSong = 0;
                    }
                }else if(types == MusicPlayerActivity.TYPE_RANDOM){
                    Random random = new Random();
                    currentSong = random.nextInt(songList.size());
                    //currentSong = (int)Math.random()*songList.size();
                }
                status = MusicActivity.ISPLAY;
                //发送广播通知Activity更改文本框
                Intent sendIntent = new Intent(MusicActivity.UPDATEUI_ACTION);
                sendIntent.putExtra("currentSong", currentSong);
                //发送广播，将被Activity中的BroadcastReceiver接收到
                sendBroadcast(sendIntent);
                //准备并播放音乐
                play(songList.get(currentSong).path);
            }
        });
    }

    public class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control){
                case MusicActivity.PLAY: // 播放或暂停
                    //原来处于没有播放状态
                    if (status == MusicActivity.NOPLAY){
                        //准备播放音乐
                        play(songList.get(currentSong).path);
                        status = MusicActivity.ISPLAY;
                    }
                    //原来处于播放状态
                    else if (status == MusicActivity.ISPLAY){
                        //暂停
                        mediaPlayer.pause();
                        status = MusicActivity.ISPAUSE; // 改变为暂停状态
                    }
                    //原来处于暂停状态
                    else if (status == MusicActivity.ISPAUSE){
                        //播放
                        mediaPlayer.start();
                        status = MusicActivity.ISPLAY; // 改变状态
                    }
                    break;
                case MusicActivity.START:
                    currentSong = intent.getIntExtra("currentSong",-1);
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                    }
                    play(songList.get(currentSong).path);
                    status = MusicActivity.ISPLAY; // 改变状态
                    break;
                case MusicActivity.NEXT:
                    if(types == MusicPlayerActivity.TYPE_ALL ){
                        currentSong ++;
                        if(currentSong > songList.size()){
                            currentSong = 0;
                        }
                    }else if(types == MusicPlayerActivity.TYPE_RANDOM){
                        Random random = new Random();
                        currentSong = random.nextInt(songList.size());
                        //currentSong = (int)Math.random()*songList.size();
                    }
                    play(songList.get(currentSong).path);
                    status = MusicActivity.ISPLAY;
                    break;
                case MusicActivity.LAST:
                    if(types == MusicPlayerActivity.TYPE_ALL ){
                        currentSong --;
                        if(currentSong < 0){
                            currentSong = songList.size();
                        }
                    }else if(types == MusicPlayerActivity.TYPE_RANDOM){
                        Random random = new Random();
                        currentSong = random.nextInt(songList.size());
                    }
                    play(songList.get(currentSong).path);
                    status = MusicActivity.ISPLAY;
                    break;
                case MusicActivity.TYPE:
                    if(types == MusicPlayerActivity.TYPE_ALL){
                        types = MusicPlayerActivity.TYPE_RANDOM;
                    }else if(types == MusicPlayerActivity.TYPE_RANDOM){
                        types = MusicPlayerActivity.TYPE_ONE;
                    }else if(types == MusicPlayerActivity.TYPE_ONE){
                        types = MusicPlayerActivity.TYPE_ALL;
                    }
                    break;
                default:
                    break;
            }
            //广播通知Activity更改图标、文本框
            Intent sendIntent = new Intent(MusicActivity.UPDATEUI_ACTION);
            sendIntent.putExtra("updateUI", status);
            sendIntent.putExtra("type",types);
            sendIntent.putExtra("currentSong", currentSong);
            //发送广播，将被Activity中的BroadcastReceiver接收到
            sendBroadcast(sendIntent);
        }
    }

    public void play(String path) {
        //播放之前要先把音频文件重置
        try {
            mediaPlayer.reset();
            //调用方法传进去要播放的音频路径
            mediaPlayer.setDataSource(path);
            //异步准备音频资源
            mediaPlayer.prepareAsync();
            //调用mediaPlayer的监听方法，音频准备完毕会响应此方法
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();//开始音频
//                    song_name.setText(songList.get(n).song);
//                    singer.setText(songList.get(n).singer);
//                    MainActivity.song_name.setText(songList.get(n).song);
//                    MainActivity.singer.setText(songList.get(n).singer);
//                    MainActivity.totaltime.setText(" / "+MusicUtils.formatTime(mediaPlayer.getDuration()));
//                    MainActivity.seekBar.setMax(mediaPlayer.getDuration());
//                    setMusic_photo();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}