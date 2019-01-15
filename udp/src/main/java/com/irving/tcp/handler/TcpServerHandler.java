package com.irving.tcp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/11 5:38 PM
 **/
public class TcpServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接了一个");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 相当于jdk ByteBuffer
        ByteBuf byteBuf = (ByteBuf) msg;
        // 全部读取
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);

        String str = new String(req);
        System.out.println(str);
    }
}
