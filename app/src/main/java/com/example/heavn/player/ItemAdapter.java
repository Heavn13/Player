package com.example.heavn.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/22 0022.
 */

public class ItemAdapter extends ArrayAdapter<Item> {
    private int resourceId;

    public ItemAdapter(Context context, int textViewResourceId, List<Item> obejects){
        super(context,textViewResourceId,obejects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Item item = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        ImageView imageView1 = view.findViewById(R.id.image);
        TextView textView1 = view.findViewById(R.id.text);
        TextView textView2 = view.findViewById(R.id.number);
        imageView1.setImageResource(item.getImageId());
        textView1.setText(item.getName());
        textView2.setText(item.getNumber());
        return view;
    }
}
