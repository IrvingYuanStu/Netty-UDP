package com.irving.tcp.handler;

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
        String msg = "建立连接成功！！！";
        System.out.println(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SignUpResp resp = (SignUpResp) msg;
        System.out.println(resp);
    }
}
