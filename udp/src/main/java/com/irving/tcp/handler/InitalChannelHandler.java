package com.irving.tcp.handler;

import com.irving.tcp.SignUpReqProto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
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
        // protobuf的32位解码器
        socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
        socketChannel.pipeline().addLast(new ProtobufDecoder(SignUpReqProto.
                SignUpReq.getDefaultInstance()));
        socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
        socketChannel.pipeline().addLast(new ProtobufEncoder());
        socketChannel.pipeline().addLast(new TcpServerHandler());
    }

    private void initHandler(ServerSocketChannel channel) {
        // 类解码器，通过weakCachingConcurrentResolver创建支持并发的弱引用缓存
        channel.pipeline().addLast(new ObjectDecoder(1024*1024,
                ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
        channel.pipeline().addLast(new ObjectEncoder());
        channel.pipeline().addLast(new TcpServerHandler());
    }

}
