package com.irving.udp.handler;

import com.irving.udp.proto.DefaultHeader;
import com.irving.udp.proto.DefaultPacket;
import com.irving.udp.proto.UDPPacket;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class UDPServerHandler extends ChannelHandlerAdapter{
	
	public int count = 1;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("与客户端建立连接成功");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (msg instanceof UDPPacket) {	// 仅处理支持的数据包
			
			UDPPacket udpPakcet = (UDPPacket) msg;
			
			DefaultPacket.Packet packet = (DefaultPacket.Packet) udpPakcet.getContent();
			DefaultHeader.Header header =  packet.getHeader();
			
			System.out.println("************************************************");
			System.out.println("消息序列号Sn: " + header.getSn());
			System.out.println("消息确认号Ack: " + header.getAck());
			System.out.println("消息类型Pt: " + header.getPtValue());
			
			System.out.println("消息体Body: " + packet.getBody());
			System.out.println("************************************************");
			System.out.println("总数："+ count++);
			Thread.sleep(15);
			
		} else { // 其他数据包继续执行 
			ctx.fireChannelRead(msg);
		}
		
	}
}
