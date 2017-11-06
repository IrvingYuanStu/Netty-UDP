package com.irving.udp.handler;

import com.irving.udp.channel.NioUdpChannel;
import com.irving.udp.proto.DefaultPacket;

import io.netty.channel.ChannelInitializer;

/**
 * 初始化类
 * @author yuanyc
 */
public class UDPClientInitializer extends ChannelInitializer<NioUdpChannel>{

	@Override
	protected void initChannel(NioUdpChannel ch) throws Exception {
		
		ch.pipeline().addLast(new UDPProtoDecoder(DefaultPacket.Packet.getDefaultInstance()));
		ch.pipeline().addLast(new UDPProtoEncoder());
		
		ch.pipeline().addLast(new UDPClientHandler());
	}

}
