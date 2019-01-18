package com.devloper.lloydfinch.recyclerviewdemo.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devloper.lloydfinch.recyclerviewdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView适配器案例
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VH> {

    private List<String> list = new ArrayList<>();

    public RecyclerAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_string, parent, false);

        //这里省事多了，不需要写复用的逻辑了，里面有自己的一套复用逻辑
        //重点是:这里复用的是ViewHolder，而不是inflate的View

        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String text = list.get(position);
        if (!TextUtils.isEmpty(text)) {
            holder.textView.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        //这里内置了一个public final的itemView

        public TextView textView;

        public VH(View itemView) {
            super(itemView); //其实就是把参数赋值给内置的itemView
            textView = itemView.findViewById(R.id.tv_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "click!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void test() {

    }
}
