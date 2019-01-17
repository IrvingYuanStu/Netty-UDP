package com.irving.tcp.handler;

import com.irving.tcp.SignUpRespProto;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
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
        socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
        socketChannel.pipeline().addLast(new ProtobufDecoder(SignUpRespProto.SignUpResp.getDefaultInstance()));
        socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
        socketChannel.pipeline().addLast(new ProtobufEncoder());
        socketChannel.pipeline().addLast(new TcpClientHandler());
    }

    private void init(SocketChannel socketChannel) {
        // 使用1024字节的单个对象编码器
        socketChannel.pipeline().addLast(new ObjectDecoder(1024,
                ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
        socketChannel.pipeline().addLast(new ObjectEncoder());
        socketChannel.pipeline().addLast(new TcpClientHandler());

    }
}
