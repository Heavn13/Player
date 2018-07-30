package com.example.heavn.player.Live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.heavn.player.Music.MusicActivity;
import com.example.heavn.player.Music.MusicAdapter;
import com.example.heavn.player.Music.MusicListActivity;
import com.example.heavn.player.Music.MusicPlayerActivity;
import com.example.heavn.player.Music.MusicUtils;
import com.example.heavn.player.Music.Song;
import com.example.heavn.player.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class LiveActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton back;
    private String name;
    private ListView listView;
    private List<Live> liveList;
    private LiveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_activity_layout);

        back = (ImageButton)findViewById(R.id.back);
        back.setOnClickListener(this);

        initView();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();
        switch (id) {
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.live_list);
        liveList = new ArrayList<>();

        Live live;
        live = new Live("香港卫视","http://61.58.60.247:9000/live/255.m3u8");
        liveList.add(live);
        live = new Live("澳门资讯","http://live4.tdm.com.mo/ch5/_definst_/info_ch5.live/playlist.m3u8");
        liveList.add(live);
        live = new Live("CCTV-6","http://183.207.249.172/ott.js.chinamobile.com/PLTV/3/224/3221225548/index.m3u8");
        liveList.add(live);
        live = new Live("TVB翡翠台","http://acm.gg/inews.m3u8");
        liveList.add(live);
        live = new Live("韩国GOODTV","http://mobliestream.c3tv.com:554/live/goodtv.sdp/playlist.m3u8");
        liveList.add(live);

        adapter = new LiveAdapter(LiveActivity.this, liveList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LiveActivity.this,LivePlayerActivity.class);
                intent.putExtra("live_path", liveList.get(position).path);
                intent.putExtra("live_name",liveList.get(position).live_name);
                startActivity(intent);
            }
        });
    }
}
