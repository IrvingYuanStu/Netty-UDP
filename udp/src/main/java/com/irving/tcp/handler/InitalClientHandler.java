package com.irving.tcp.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/11 5:55 PM
 **/
public class InitalClientHandler extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) {
        // 使用1024字节的单个对象编码器
        socketChannel.pipeline().addLast(new ObjectDecoder(1024,
                ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
        socketChannel.pipeline().addLast(new ObjectEncoder());
        socketChannel.pipeline().addLast(new TcpClientHandler());
    }
}
