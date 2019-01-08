package com.devloper.lloydfinch.recyclerviewdemo.listview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.devloper.lloydfinch.recyclerviewdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ListView适配器案例
 */
public class ListViewAdapter extends BaseAdapter {

    private List<String> list = new ArrayList<>();

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView == null ?
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_string, parent, false)
                : convertView;

        ViewHolder holder = (ViewHolder) itemView.getTag();
        if (holder == null) {
            holder = new ViewHolder(itemView);
            convertView.setTag(holder);
        }

        //这里不写复用逻辑，也就两行代码
        //复用的是convertView不是ViewHolder，如果不处理还得构造(就意味着findViewById，这是个遍历操作，耗时)

        return itemView;
    }


    public static class ViewHolder {
        public View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
