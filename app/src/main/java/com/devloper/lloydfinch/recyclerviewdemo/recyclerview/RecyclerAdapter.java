package com.devloper.lloydfinch.recyclerviewdemo.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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

    private RecyclerView mRecyclerView;

    private View headerView;
    private View footerView;
    private View emptyView;

    private List<String> list = new ArrayList<>();

    public void setRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    public RecyclerAdapter(List<String> list) {
        this.list = list;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public boolean hadHeader() {
        return headerView != null;
    }

    public boolean hadFooter() {
        return footerView != null;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (ViewHolderType.HEADER.ordinal() == viewType && headerView != null) {
            return new VH(headerView, viewType);
        } else if (ViewHolderType.FOOTER.ordinal() == viewType && footerView != null) {
            return new VH(footerView, viewType);
        } else {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_string, parent, false);

            //这里省事多了，不需要写复用的逻辑了，里面有自己的一套复用逻辑
            //重点是:这里复用的是ViewHolder，而不是inflate的View

            return new VH(itemView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (position == 0 && headerView != null) {
            //处理headerView的数据
        } else if (position == list.size() - 1 + getExtraViewCount() && footerView != null) {
            //处理footerView的数据
        } else {
            //这里注意数据的偏差
            int realPosition = position;
            if (headerView != null) {
                realPosition = position - 1;
            }
            String text = list.get(realPosition);
            if (!TextUtils.isEmpty(text)) {
                holder.textView.setText(text);
            }
        }
    }

    @Override
    public int getItemCount() {
        int size = list.size();
        if (emptyView != null) {
            if (size > 0) {
                //数据不为空，隐藏EmptyView
                if (emptyView.getVisibility() == View.VISIBLE) {
                    emptyView.setVisibility(View.GONE);
                }
            } else {
                //数据为空，显示EmptyView
                if (emptyView.getVisibility() != View.VISIBLE) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        }

        //添加了header和footer，注意itemCount的返回值
        //当没有数据的时候，不显示header和footer
        if (size > 0) {
            size += getExtraViewCount();
        }

        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return ViewHolderType.HEADER.ordinal();
        } else if (position == list.size() - 1 + getExtraViewCount() && footerView != null) {
            return ViewHolderType.FOOTER.ordinal();
        } else {
            return ViewHolderType.NORMAL.ordinal();
        }
    }


    private int getExtraViewCount() {
        int count = 0;
        if (headerView != null) {
            count++;
        }
        if (footerView != null) {
            count++;
        }
        return count;
    }

    public static class VH extends RecyclerView.ViewHolder {

        //这里内置了一个public final的itemView

        public TextView textView;

        public VH(View itemView, int viewType) {
            super(itemView); //其实就是把参数赋值给内置的itemView
            if (ViewHolderType.HEADER.ordinal() == viewType) {
                //处理headerView的一些问题
            } else if (ViewHolderType.FOOTER.ordinal() == viewType) {
                //处理footerView的一些问题
            } else {
                textView = itemView.findViewById(R.id.tv_text);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "click!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    enum ViewHolderType {
        HEADER,
        FOOTER,
        NORMAL
    }
}
