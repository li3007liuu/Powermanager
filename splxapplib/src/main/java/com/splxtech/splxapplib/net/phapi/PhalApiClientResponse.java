package com.splxtech.splxapplib.net.phapi;

/**
 * 接口返回结果
 *
 * - 与接口返回的格式对应，即有：ret/data/msg
 */
public class PhalApiClientResponse {

    private int ret;
    private String data;
    private String msg;

    public PhalApiClientResponse() {

    }

    public PhalApiClientResponse(int ret, String data, String msg) {
        this.ret = ret;
        this.data = data;
        this.msg = msg;
    }

    public PhalApiClientResponse(int ret, String data) {
    	this(ret, data, "");
    }

    public PhalApiClientResponse(int ret) {
    	this(ret, "", "");
    }

    public int getRet() {
        return this.ret;
    }

    public void setRet( int ret) {
        this.ret = ret;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}