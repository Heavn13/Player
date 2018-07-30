package com.example.heavn.player.Music;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.heavn.player.R;

import java.util.List;

public class LittleMusicAdapter extends BaseAdapter {
    private Context context;
    private List<Song> list;
    public LittleMusicAdapter(Context context, List<Song> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.music_ilittle_tem, null);
            //实例化对象
            holder.song = (TextView) view.findViewById(R.id.song);
            holder.singer = (TextView) view.findViewById(R.id.singer);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //给控件赋值
        holder.song.setText(list.get(i).song.toString());
        holder.singer.setText(list.get(i).singer.toString());
//        //时间需要转换一下
//        int duration = list.get(i).duration;
//        String time = MusicUtils.formatTime(duration);
//        holder.duration.setText(time);
//        holder.position.setText(i+1+"");

        return view;
    }
    class ViewHolder{
        TextView song;//歌曲名
        TextView singer;//歌手
        TextView duration;//时长

    }

}
