package com.frameclient.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.frameclient.adapter.CommonListAdapter;
import com.frameclient.adapter.ViewHolder;
import com.frameclient.utils.ApiRequest;
import com.frameclient.utils.NewsListBean;

import java.util.ArrayList;
import java.util.List;

public class NewsListActivity extends Activity {

    private List<NewsListBean.ResultBean> newsList;
    private CommonListAdapter<NewsListBean.ResultBean> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list_activiry);

        GridView gridView = (GridView) findViewById(R.id.grid_view);

        newsList = new ArrayList<>();

        adapter = new CommonListAdapter<NewsListBean.ResultBean>(this, newsList, R.layout.item_news) {
            @Override
            public void convert(ViewHolder helper, NewsListBean.ResultBean item, int position) {
                helper.setText(R.id.txt_name, item.getTitle());
            }
        };
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(NewsListActivity.this, NewsWebActivity.class);
                intent.putExtra("url",newsList.get(i).getUrl());
                startActivity(intent);
            }
        });

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setHttpResultCallBack(new ApiRequest.HttpResultCallBack() {
            @Override
            public void pushAlarmSuccess() {

            }

            @Override
            public void pushAlarmFail() {

            }

            @Override
            public void onNewsListResult(List<NewsListBean.ResultBean> list) {
                newsList.clear();
                newsList.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void getNewsListFail() {
                Toast.makeText(NewsListActivity.this, "，新闻列表获取失败", Toast.LENGTH_LONG).show();

            }
        });

        apiRequest.getAllNewsList();
    }
}
