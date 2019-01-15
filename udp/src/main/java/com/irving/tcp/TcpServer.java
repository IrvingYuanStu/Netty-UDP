package com.irving.tcp;

import com.irving.tcp.handler.InitalChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/11 5:27 PM
 **/
public class TcpServer {
    public static void main(String[] args) {
        // Netty主从结构
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap boot = new ServerBootstrap();
        // 配置 处理线程组, channel, handler
        boot.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new InitalChannelHandler());

        try {
            // 启动服务器
            ChannelFuture future = boot.bind(9900).sync();
            // 监听端口关闭的操作
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
