package com.irving.tcp.handler;

import com.irving.tcp.SignUpRespProto;
import com.irving.tcp.bean.SignUpResp;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/11 5:56 PM
 **/
public class TcpClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // netty、NIO操作的数据单位都是ByteBuffer数组
        System.out.println("建立连接成功...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SignUpRespProto.SignUpResp resp = (SignUpRespProto.SignUpResp) msg;
        int reqId = resp.getReqId();
        int statusCode = resp.getStatusCode();
        String resultMsg = resp.getResultMsg();
        System.out.println("收到返回值，reqId="+reqId+" ,statusCode="+statusCode+" ,resultMsg="+resultMsg);
    }
}
