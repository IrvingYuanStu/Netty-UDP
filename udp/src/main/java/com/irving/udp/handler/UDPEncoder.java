package com.irving.udp.handler;

import com.irving.udp.proto.UDPProto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;

/**
 * UDP编码器
 * UDPProto --> DatagramPacket
 */
@SuppressWarnings("deprecation")
public class UDPEncoder extends ChannelOutboundHandlerAdapter{
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		// TODO Auto-generated method stub
		if (msg instanceof UDPProto) {
			UDPProto udpProto = (UDPProto) msg;
			ctx.write(new DatagramPacket((ByteBuf) udpProto.getMsg(), udpProto.getInetSocketAddress()),promise);
		} else {
			ctx.write(msg, promise);
		}
	}

}
