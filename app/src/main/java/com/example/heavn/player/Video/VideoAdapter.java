package com.example.heavn.player.Video;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heavn.player.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private Context context;
    private List<Video> list;
    private OnItemClickListener mOnItemClickListener;
    public VideoAdapter(Context context, List<Video> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Video video = list.get(position);
        //给控件赋值
        holder.video_name.setText(video.video_name.toString());
        //时间需要转换一下
        int duration = video.duration;
        String time = VideoUtils.formatTime(duration);
        holder.duration.setText(time);
        holder.img.setImageBitmap(VideoUtils.getVideoThumbnail(video.path,200,120, MediaStore.Video.Thumbnails.MINI_KIND));

        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });

        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView video_name;//视频名称
        TextView duration;//时长
        ImageView img;//视频略缩图

        public  ViewHolder(View view){
            super(view);
            //实例化对象
            video_name =  view.findViewById(R.id.video_name);
            duration =  view.findViewById(R.id.time);
            img = view.findViewById(R.id.video_image);
        }

    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener = onItemClickListener;
    }

}
