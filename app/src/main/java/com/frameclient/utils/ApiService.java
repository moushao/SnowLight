package com.frameclient.utils;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * Created by Administrator on 2017/12/28.
 */

public interface ApiService {

    /**
     * 报警系统
     */
    @POST("push_alarm")
    Observable<ResultBean> pushAlarm(
            @Body RequestBody jsonStr
    );

    /**
     *获取前十条新闻 
     */
    @GET("ssmvideo/NewsController/listNews")
    Observable<NewsListBean> getNews();

    /**
     * 获取全部新闻
     */
    @GET("ssmvideo/NewsController/get")
    Observable<NewsListBean> getAllNews();

}
