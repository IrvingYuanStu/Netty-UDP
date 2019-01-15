package com.irving.tcp.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/11 5:55 PM
 **/
public class InitalClientHandler extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new TcpClientHandler());
    }
}
