package com.irving.udp.handler;

import com.irving.udp.proto.UDPProto;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * UDPPacket解码器P
 * UDPPacket --> UDPProto(socketAddres, bytebuf)
 */
public class UDPDecoder extends SimpleChannelInboundHandler<DatagramPacket>{

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
		ctx.fireChannelRead(new UDPProto(packet.sender(), packet.content()));
	}

}
