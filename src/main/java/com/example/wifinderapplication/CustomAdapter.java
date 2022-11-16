package com.example.wifinderapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private int layoutId;
    private ArrayList<MyItem> data;

    CustomAdapter(Context context, int layoutId, ArrayList<MyItem> data){
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layoutId, parent, false);
        }

        ((ImageView)convertView.findViewById(R.id.wifiBars)).setImageResource(data.get(position).vectorID);
        ((TextView)convertView.findViewById(R.id.ssid)).setText(data.get(position).ssid);
        ((TextView)convertView.findViewById(R.id.security)).setText(data.get(position).security);
        ((TextView)convertView.findViewById(R.id.level)).setText(data.get(position).level);
        ((TextView)convertView.findViewById(R.id.frequency)).setText(data.get(position).frequency);
        ((TextView)convertView.findViewById(R.id.distance)).setText(data.get(position).distance);
        ((TextView)convertView.findViewById(R.id.bssid)).setText(data.get(position).bssid);

        if(data.get(position).vectorID == R.drawable.signal_level_4){
            ((ImageView)convertView.findViewById(R.id.wifiBars)).setColorFilter(Color.argb(255, 198, 15, 216));
        } else if (data.get(position).vectorID == R.drawable.signal_level_3){
            ((ImageView)convertView.findViewById(R.id.wifiBars)).setColorFilter(Color.argb(255, 0, 190, 63));
        } else if (data.get(position).vectorID == R.drawable.signal_level_2){
            ((ImageView)convertView.findViewById(R.id.wifiBars)).setColorFilter(Color.argb(255, 221, 116, 10));
        } else {
            ((ImageView)convertView.findViewById(R.id.wifiBars)).setColorFilter(Color.argb(255, 190, 14, 0));
        }

        //highlight selected item
        ListView list = (ListView)parent;
        if(list.isItemChecked(position)){
        }

        return convertView;
    }
}
