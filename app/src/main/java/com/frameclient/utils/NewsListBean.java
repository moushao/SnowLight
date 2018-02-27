package com.frameclient.utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/12/28.
 */

public class NewsListBean implements Serializable {


    /**
     * code : 0
     * msg : 成功
     * result : [{"title":"","url":""},{"title":"","url":""},{"title":"","url":""},{"title":"","url":""},{"title":"","url":""}]
     */

    private int code;
    private String msg;
    /**
     * title :
     * url :
     */

    private List<ResultBean> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String title;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
