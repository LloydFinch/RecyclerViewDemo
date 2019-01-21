package com.devloper.lloydfinch.recyclerviewdemo.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义的RecyclerView,扩展了一系列模仿ListView的便捷方法
 */
public class CusRecyclerView extends RecyclerView {

    private View emptyView;
    private View headerView;
    private View footerView;

    private RecyclerAdapter mAdapter;

    public CusRecyclerView(Context context) {
        super(context);
    }

    public CusRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CusRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof RecyclerAdapter) {
            super.setAdapter(adapter);
            mAdapter = (RecyclerAdapter) adapter;
            mAdapter.setRecyclerView(this);
            mAdapter.setHeaderView(headerView);
            mAdapter.setFooterView(footerView);
            mAdapter.setEmptyView(emptyView);
        } else {
            throw new IllegalArgumentException("the adapter must be RecyclerAdapter");
        }
    }

    public void addEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public void addHeader(View headerView) {
        this.headerView = headerView;
        if (mAdapter != null) {
            mAdapter.setHeaderView(headerView);
        }
    }

    public void addFooter(View footerView) {
        this.footerView = footerView;
        if (mAdapter != null) {
            mAdapter.setFooterView(footerView);
        }
    }

}
