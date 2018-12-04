package com.irving.udp.server;

import com.irving.udp.channel.NioUdpChannel;
import com.irving.udp.handler.UDPServerInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * UDP 协议服务器
 * @author yuanyc
 */
public class UDPServer {
	
	public static void main(String[] args) throws InterruptedException {
		
		EventLoopGroup eventGroup = new NioEventLoopGroup();
		
		try{
			Bootstrap bootStrap = new Bootstrap();
			bootStrap.group(eventGroup).channel(NioUdpChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new UDPServerInitializer());
			
			System.out.println("UDP server is starting");
			
			// 绑定端口，同步等待成功
			ChannelFuture f = bootStrap.bind("127.0.0.1", 9900).sync();
			
			// 等待服务端关闭
			f.channel().closeFuture().sync();
		} finally {
			// 释放reactor线程资源
			eventGroup.shutdownGracefully();
		}
	}

}
