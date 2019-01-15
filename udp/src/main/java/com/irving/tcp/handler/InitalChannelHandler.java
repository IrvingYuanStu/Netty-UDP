package com.irving.tcp.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @Description channelHandler处理器
 * @Author yuanyc
 * @Date 2019/1/11 5:37 PM
 **/
public class InitalChannelHandler extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().
                addLast(new TcpServerHandler());
    }
}
