package com.example.heavn.player.Live;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.heavn.player.Music.MusicUtils;
import com.example.heavn.player.Music.Song;
import com.example.heavn.player.R;

import java.util.List;

public class LiveAdapter extends BaseAdapter {
    private Context context;
    private List<Live> list;
    public LiveAdapter(Context context, List<Live> list) {
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
            view = View.inflate(context, R.layout.live_item, null);
            //实例化对象
            holder.live_name = (TextView) view.findViewById(R.id.live_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //给控件赋值
        holder.live_name.setText(list.get(i).live_name.toString());

//        holder.position.setText(i+1+"");

        return view;
    }
    class ViewHolder{
        TextView live_name;//视频直播名

    }

}
