package com.example.wifinderapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailsAdapter extends BaseAdapter {
    private Context context;
    private int layoutId;
    private ArrayList<DetailsItem> data;

    DetailsAdapter(Context context, int layoutId, ArrayList<DetailsItem> data){
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

        ((TextView)convertView.findViewById(R.id.type)).setText(data.get(position).type);
        ((TextView)convertView.findViewById(R.id.value)).setText(data.get(position).value);

        //highlight selected item
        ListView list = (ListView)parent;
        if(list.isItemChecked(position)){
        }

        return convertView;
    }
}
