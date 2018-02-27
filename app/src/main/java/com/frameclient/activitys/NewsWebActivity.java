package com.frameclient.activitys;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

public class NewsWebActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web);

        WebView webView = (WebView) findViewById(R.id.webView);
        String url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            webView.loadUrl("http://news.qq.com/");
        } else {
            webView.loadUrl(url);
        }
    }
}
