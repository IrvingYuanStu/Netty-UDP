package com.irving.tcp.bean;

import java.io.Serializable;

/**
 * @Description 注册请求对象
 * @Author yuanyc
 * @Date 2019/1/15 5:33 PM
 **/
public class SignUpReq implements Serializable {

    private int reqId;
    private String userCode;
    private String passWord;

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "SignUpReq{" +
                "reqId=" + reqId +
                ", userCode='" + userCode + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}
