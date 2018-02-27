package com.frameclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by xxx on 2016/5/25.
 * 万能适配器
 */
public abstract class CommonListAdapter<T> extends BaseAdapter {

    public LayoutInflater mInflater;
    private Context mContext;//上下文
    private List<T> mDatas;//数据源
    private final int mItemLayoutId;//要加载的布局的ID

    public CommonListAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        convert(viewHolder, (T) getItem(position),position);
        return viewHolder.getConvertView();

    }

    public abstract void convert(ViewHolder helper, T item,int position);

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
                position);
    }

}
