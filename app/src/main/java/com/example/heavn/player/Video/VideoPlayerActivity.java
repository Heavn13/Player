package com.example.heavn.player.Video;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.player.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/3/26 0026.
 */

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{
//    private VideoView videoView;
//    private MediaController mediaController;
    private ImageView back;

    private SurfaceView surfaceView = null;
    private SurfaceHolder surfaceHolder = null;
    private MediaPlayer mediaPlayer = null;

    // 自定义的控制条及其上的控件
    private View controllerView;
    private PopupWindow popupWindow;

    private ImageView play;
    private ImageView fullscreen;
    private SeekBar seekBar;
    private TextView video_playTime;
    private TextView video_duration;
    private TextView video_name;
    private TextView system_time;
    private String filePath = null;
    private int current;
    private List<Video> videoList = new ArrayList<Video>();

    private float densityRatio = 1.0f; // 密度比值系数（密度比值：一英寸中像素点除以160）

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            // 又回到了主线程
            showOrHiddenController();
        }
    };


    // 设置定时器
    private Timer timer = null;
    private final static int WHAT = 0;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case WHAT:
                    if (mediaPlayer != null) {
                        int currentPlayer = mediaPlayer.getCurrentPosition();
                        if (currentPlayer > 0) {
                            mediaPlayer.getCurrentPosition();
                            video_playTime.setText(formatTime(currentPlayer));
                            system_time.setText(formatSystemTime(System.currentTimeMillis()));
                            // 让seekBar也跟随改变
                            int progress = (int) ((currentPlayer / (float) mediaPlayer.getDuration()) * 100);
                            seekBar.setProgress(progress);
                        } else {
                            system_time.setText(formatSystemTime(System.currentTimeMillis()));
                            video_playTime.setText("00:00");
                            seekBar.setProgress(0);
                        }
                    }

                    break;

                default:
                    break;
            }
        };
    };

    // 自动隐藏自定义播放器控制条的时间
    private static final int HIDDEN_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_play_interface_layout);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        //设置为全屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Intent intent = getIntent();
        current = intent.getIntExtra("current",-1);

        videoList = VideoUtils.getVideoData(this);

        filePath = videoList.get(current).path.toString();

        initView();

        initController();

        initMediaPlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_back:
                //否则窗体会泄露
                popupWindow.dismiss();
                finish();
                break;
            case R.id.fullscreen:
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    fullscreen.setBackgroundResource(R.drawable.horizontal_display);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    // 重新设置surfaceView的高度和宽度
//                    surfaceView.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
//                    surfaceView.getLayoutParams().height = (int) (260 * densityRatio);
                } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    fullscreen.setBackgroundResource(R.drawable.verticle_display);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    surfaceView.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
//                    surfaceView.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
                }

//                surfaceView.setLayoutParams(surfaceView.getLayoutParams());
                break;
            case R.id.imageView_play:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                } else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }
                break;
            default:
                break;

        }
    }

    private String formatTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    private String formatSystemTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(new Date(time));
    }

    private void initController() {

        controllerView = getLayoutInflater().inflate(R.layout.video_controller, null);

        // 初始化popopWindow
        popupWindow = new PopupWindow(controllerView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, true);

        play =  controllerView.findViewById(R.id.imageView_play);
        play.setOnClickListener(this);
        fullscreen = controllerView.findViewById(R.id.fullscreen);
        // 实现全屏和退出全屏(内容物横竖屏,不是屏幕的横竖屏)
        fullscreen.setOnClickListener(this);
        back = controllerView.findViewById(R.id.video_back);
        back.setOnClickListener(this);
        seekBar = controllerView.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        video_playTime = controllerView.findViewById(R.id.textView_playtime);
        video_duration = controllerView.findViewById(R.id.textView_totaltime);
        video_name = controllerView.findViewById(R.id.video_name);
        video_name.setText(videoList.get(current).video_name);
        system_time = controllerView.findViewById(R.id.sys_time);


    }

    private void showOrHiddenController() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            // 将dp转换为px
            //int controllerHeightPixel = (int) (densityRatio * 50);
            popupWindow.showAsDropDown(surfaceView, 0, -surfaceView.getHeight());
            // 延时执行
            handler.postDelayed(r, HIDDEN_TIME);
        }
    }

    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            // 1,创建MediaPlay对象
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                mediaPlayer.setLooping(false);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // 表示准备完成，设置总的时长，使用时间格式化工具

                // String duration = mediaPlayer.getDuration() ;
                video_duration.setText(formatTime(mediaPlayer.getDuration()));
                // 初始化定时器
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        handler.sendEmptyMessage(WHAT);
                    }
                }, 0, 1000);
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();
                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                current ++;
                if(current >= videoList.size()){
                    current = 0;
                }
                filePath = videoList.get(current).path;
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                    video_name.setText(videoList.get(current).video_name);
                    mediaPlayer.setLooping(false);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initView() {
        // TODO Auto-generated method stub
        densityRatio = getResources().getDisplayMetrics().density; // 表示获取真正的密度

        //imageView_main_show = (ImageView) findViewById(R.id.imageView_main_play);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView_main);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                if (mediaPlayer != null) {
                    mediaPlayer.setDisplay(surfaceHolder);
                    // mediaPlayer.start() ;

                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // TODO Auto-generated method stub

            }
        });

        // 设置屏幕的触摸监听
        surfaceView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 表示在点击的瞬间就显示控制条
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        showOrHiddenController();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        timer.cancel();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        handler.removeCallbacksAndMessages(null);
    }

    // 表示手指拖动seekbar完毕，手指离开屏幕会触发以下方法
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 让计时器延时执行
        handler.postDelayed(r, HIDDEN_TIME);
    }

    // 在手指正在拖动seekBar，而手指未离开屏幕触发的方法
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // 让计时器取消计时
        handler.removeCallbacks(r);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int playtime = progress * mediaPlayer.getDuration() / 100;
            mediaPlayer.seekTo(playtime);
        }

    }

}
