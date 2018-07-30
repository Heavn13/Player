package com.example.heavn.player;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.heavn.player.Live.LiveActivity;
import com.example.heavn.player.Live.LivePlayerActivity;
import com.example.heavn.player.Music.MusicActivity;
import com.example.heavn.player.Video.VideoActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button music;
    private Button video;
    private Button live;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        music = (Button)findViewById(R.id.music);
        music.setOnClickListener(this);
        video = (Button)findViewById(R.id.video);
        video.setOnClickListener(this);
        live = (Button)findViewById(R.id.live);
        live.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.music:
                intent = new Intent(MainActivity.this,MusicActivity.class);
                startActivity(intent);
                break;
            case R.id.video:
                intent = new Intent(MainActivity.this,VideoActivity.class);
                startActivity(intent);
                break;
            case R.id.live:
                intent = new Intent(MainActivity.this,LiveActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
