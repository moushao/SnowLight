package com.frameclient.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.InflaterInputStream;


/**
 * Created by xxx on 2016/5/25.
 */
public class ViewHolder {
    private final SparseArray<View> mViews;
    public int mPosition;
    private View mConvertView;
    private ItemClick mItemClick;


    private ViewHolder(Context context, ViewGroup parent, int layoutId,
                       int position, ItemClick mItemClick) {
        this.mPosition = position;
        this.mItemClick = mItemClick;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        // setTag
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position, ItemClick mItemClick) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position, mItemClick);
        }
        return (ViewHolder) convertView.getTag();
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text, int position) {
        TextView view = getView(viewId);
        view.setText(text);
        view.setKeyListener(null);
        view.setTag(position);
        mPosition = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClick.onItemClick(mPosition);
            }
        });
        return this;
    }

    public ViewHolder setText(int viewId, int resid) {
        TextView view = getView(viewId);
        view.setText(resid);
        return this;
    }

    public ViewHolder setTextThroughLine(int viewId) {
        TextView view = getView(viewId);
        view.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线（删除线）
        view.getPaint().setAntiAlias(true);//
        return this;
    }


    /**
     * 设置文字颜色
     *
     * @param viewId
     * @param resid
     * @return
     */
    public ViewHolder setTextColor(int viewId, int resid) {
        TextView view = getView(viewId);
        view.setTextColor(resid);
        return this;
    }

    public ViewHolder setTextBackGround(int viewId, int resid) {
        TextView view = getView(viewId);
        view.setBackgroundColor(resid);
        return this;
    }

    public ViewHolder setBackGround(int viewId, int resid) {
        View view = getView(viewId);
        view.setBackgroundColor(resid);
        return this;
    }

    public ViewHolder setBackGroundDrawable(int viewId, int resid) {
        View view = getView(viewId);
        view.setBackgroundResource(resid);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }


    public ViewHolder setVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }


    public int getPosition() {
        return mPosition;
    }

    public interface ItemClick {

        void onItemClick(int position);
    }
}
