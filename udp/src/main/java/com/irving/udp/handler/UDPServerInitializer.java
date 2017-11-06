package com.irving.udp.handler;

import com.irving.udp.channel.NioUdpChannel;
import com.irving.udp.proto.DefaultPacket;

import io.netty.channel.ChannelInitializer;

/**
 * 通道初始化处理器
 * @author yuanyc
 */
public class UDPServerInitializer extends ChannelInitializer<NioUdpChannel>{

	@Override
	protected void initChannel(NioUdpChannel ch) throws Exception {
		
		// UDP Protobuf协议解析
		ch.pipeline().addLast(new UDPProtoDecoder(DefaultPacket.Packet.getDefaultInstance()));
		ch.pipeline().addLast(new UDPProtoEncoder());
		
		//业务处理器
		ch.pipeline().addLast(new UDPServerHandler());
		
	}

}
