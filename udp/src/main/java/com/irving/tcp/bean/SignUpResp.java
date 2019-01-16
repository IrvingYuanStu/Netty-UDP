package com.irving.tcp.bean;

import java.io.Serializable;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/15 5:35 PM
 **/
public class SignUpResp implements Serializable {

    private int reqId;
    private int statusCode;
    private String resultMsg;

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    @Override
    public String toString() {
        return "SignUpResp{" +
                "reqId=" + reqId +
                ", statusCode=" + statusCode +
                ", resultMsg='" + resultMsg + '\'' +
                '}';
    }
}
