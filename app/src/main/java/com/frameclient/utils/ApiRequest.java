package com.frameclient.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/28.
 */

public class ApiRequest {

    private static final int DEFAULT_TIMEOUT = 20;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private final ApiService apiService;
    private HttpResultCallBack httpResultCallBack;

    public void setHttpResultCallBack(HttpResultCallBack httpResultCallBack) {
        this.httpResultCallBack = httpResultCallBack;
    }

    public ApiRequest() {
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();

        Gson gson = new GsonBuilder().create();
        /*StringBuffer baseUrl = new StringBuffer();
        baseUrl.append("http://");
        baseUrl.append(Constants.BASE_IP);
        baseUrl.append(":8081");*/
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void postAlarm(String name, int type) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", name);
        map.put("type", type);
        apiService.pushAlarm(toJson(map)).
                subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResultBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("http", e.getMessage() + "");
                        httpResultCallBack.pushAlarmFail();
                    }

                    @Override
                    public void onNext(ResultBean resultBean) {
                        httpResultCallBack.pushAlarmSuccess();
                    }
                });
    }

    public void getNewsList() {
        String json = "{\"code\":0,\"msg\":\"成功\",\"result\":[{\n" +
                "\"title\":\"全国人民代表大会常务委员会任免名单\",\n" +
                "\"url\":\"http://m2.people.cn/r/MV8wXzEwMzE5Mzc1XzIwM18xNTE0MzY4MDY2?tt_group_id=6504162017996177933" +
                "\"\n" +
                "},\n" +
                "{\n" +
                "\"title\":\"武警部队归中央军委建制，不再列国务院序列\",\n" +
                "\"url\":\"https://news.qq.com/a/20171227/022792.htm\"\n" +
                "},\n" +
                "{\n" +
                "\"title\":\"全国铁路今起大调图：石济高铁开通 “复兴号”再扩容\",\n" +
                "\"url\":\"http://new.qq.com/omn/20171228A03X0G.html\"\n" +
                "},\n" +
                "{\n" +
                "\"title\":\"烟叶税法、船舶吨税法明年施行 这里面有哪些看点\",\n" +
                "\"url\":\"http://new.qq.com/omn/20171228A03X0G.html\"\n" +
                "},\n" +
                "{\n" +
                "\"title\":\"台媒：岛内“反中”势力又抬头 蔡政府应及早预设“防火墙”\",\n" +
                "\"url\":\"http://new.qq.com/omn/20171228A03X0G.html\"\n" +
                "}\n" +
                "]}";
        Gson gson = new GsonBuilder().create();
        NewsListBean newsListBean = gson.fromJson(json, NewsListBean.class);

        httpResultCallBack.onNewsListResult(newsListBean.getResult());
    }

    public void getAllNewsList() {
        apiService.getAllNews().
                subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewsListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("http", e.getMessage() + "");
                        httpResultCallBack.getNewsListFail();
                    }

                    @Override
                    public void onNext(NewsListBean resultBean) {
                        httpResultCallBack.onNewsListResult(resultBean.getResult());
                    }
                });
    }
    //    public void getNewsList(){
    //        apiService.getNews().
    //                subscribeOn(Schedulers.io())
    //                .unsubscribeOn(Schedulers.io())
    //                .observeOn(AndroidSchedulers.mainThread())
    //                .subscribe(new Subscriber<NewsListBean>() {
    //                    @Override
    //                    public void onCompleted() {
    //
    //                    }
    //
    //                    @Override
    //                    public void onError(Throwable e) {
    //                        Log.e("http", e.getMessage() + "");
    //                        httpResultCallBack.getNewsListFail();
    //                    }
    //
    //                    @Override
    //                    public void onNext(NewsListBean resultBean) {
    //                        httpResultCallBack.onNewsListResult(resultBean.getResult());
    //                    }
    //                });
    //    }

    //    }

    private RequestBody toJson(Map parameters) {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new Gson().toJson
                (parameters));
    }

    public interface HttpResultCallBack {

        void pushAlarmSuccess();

        void pushAlarmFail();

        void onNewsListResult(List<NewsListBean.ResultBean> list);

        void getNewsListFail();
    }

}
