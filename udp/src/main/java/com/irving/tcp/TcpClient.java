package com.irving.tcp;

import com.irving.tcp.handler.InitalClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/11 5:52 PM
 **/
public class TcpClient {
    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        // 客户端操作
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new InitalClientHandler());
        try {
            ChannelFuture channelFuture = bootstrap.connect("localhost", 9900).sync();

            SignUpReqProto.SignUpReq.Builder builder = SignUpReqProto.SignUpReq.newBuilder();
            builder.setReqId(100);
            builder.setUserCode("admin");
            builder.setPassword("admin123");
            channelFuture.channel().writeAndFlush(builder.build());

            // 等待收到信号后，再关闭主线程
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
