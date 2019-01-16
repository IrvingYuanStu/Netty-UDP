package com.irving.tcp.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @Description channelHandler处理器
 * @Author yuanyc
 * @Date 2019/1/11 5:37 PM
 **/
public class InitalChannelHandler extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel socketChannel) {
        // 类解码器，通过weakCachingConcurrentResolver创建支持并发的弱引用缓存
        socketChannel.pipeline().addLast(new ObjectDecoder(1024*1024,
                ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
        socketChannel.pipeline().addLast(new ObjectEncoder());
        socketChannel.pipeline().addLast(new TcpServerHandler());
    }

}
