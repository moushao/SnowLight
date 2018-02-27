package com.frameclient.utils;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/28.
 */

public class ResultBean  implements Serializable{


    /**
     * code : 0
     * msg : 成功
     * result : null
     */

    private int code;
    private String msg;
    private Object result;

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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
