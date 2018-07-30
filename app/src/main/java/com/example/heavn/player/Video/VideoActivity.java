package com.example.heavn.player.Video;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.heavn.player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class VideoActivity extends AppCompatActivity implements View.OnClickListener{
    private List<Video> videoList;
    private VideoAdapter adapter;
    private RecyclerView recyclerView;
    private ImageButton back;
    private ImageButton search;
    private EditText edit;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_list_layout);

        back = (ImageButton)findViewById(R.id.back);
        back.setOnClickListener(this);

        search = (ImageButton)findViewById(R.id.search);
        search.setOnClickListener(this);

        edit = (EditText)findViewById(R.id.edit);
        edit.setOnClickListener(this);

        try{
            initView();
        }catch (Exception e){
            Toast.makeText(VideoActivity.this,"未获取到视频源",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.search:
                if(edit.getVisibility() == View.INVISIBLE) edit.setVisibility(View.VISIBLE);
                else name = edit.getText().toString();
                for(int i = 0;i < videoList.size();i++){
                    if(videoList.get(i).video_name.equals(name)) {
                        recyclerView.setSelected(true);
                        recyclerView.setFocusable(true);
                    }
                }
                break;
            default:
                break;

        }
    }

    private void initView() {
        recyclerView = (RecyclerView)findViewById(R.id.video_list);
        videoList = new ArrayList<>();
        //把扫描到的视频赋值给list
        videoList = VideoUtils.getVideoData(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        //设置RecyclerView 布局
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new VideoAdapter(VideoActivity.this, videoList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(VideoActivity.this,VideoPlayerActivity.class);
                intent.putExtra("current",position);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }
}
