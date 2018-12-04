package com.irving.udp.client;

import com.irving.udp.channel.NioUdpChannel;
import com.irving.udp.handler.UDPClientInitializer;
import com.irving.udp.proto.DefaultHeader;
import com.irving.udp.proto.DefaultHeader.Header;
import com.irving.udp.proto.DefaultHeader.ProtoType;
import com.irving.udp.proto.DefaultPacket;
import com.irving.udp.proto.DefaultPacket.Packet;
import com.irving.udp.proto.pojo.UDPPacket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.Scanner;

/**
 * udp客户端
 */
public class UDPClient {
	public static void main(String[] args) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioUdpChannel.class)
		.option(ChannelOption.SO_BROADCAST, true)
		.option(ChannelOption.SO_SNDBUF, 1024 * 1024)	// 发送缓冲区
		.option(ChannelOption.SO_RCVBUF, 1024 * 1024)	// 接收缓冲区
		.handler(new UDPClientInitializer());
		
		Channel ch = b.bind(9901).sync().channel();
		Thread.sleep(1000);
		
		sendMsg(ch);
	}
	
	public static void sendMsg(Channel ch) throws InterruptedException{
		DefaultPacket.Packet.Builder packetBuilder = DefaultPacket.Packet.newBuilder();
		DefaultHeader.Header.Builder headBuiler = DefaultHeader.Header.newBuilder();
		
		int i = 1000;
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			headBuiler.setSn(i);
			headBuiler.setAck(i - 1);
			headBuiler.setAckbits(2);
			headBuiler.setPt(ProtoType.DATA);

			Header header = headBuiler.build();

			packetBuilder.setHeader(header);
			packetBuilder.setBody(scanner.nextLine());

			Packet packet = packetBuilder.build();

			UDPPacket udpPacket = new UDPPacket();
			udpPacket.setContent(packet);
			udpPacket.setDst("127.0.0.1:9900");
			ch.writeAndFlush(udpPacket);
		}
	}
}
