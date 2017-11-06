package com.irving.udp.handler;

import com.irving.udp.common.IDGenerator;
import com.irving.udp.proto.DefaultHeader;
import com.irving.udp.proto.DefaultHeader.Header;
import com.irving.udp.proto.DefaultHeader.ProtoType;
import com.irving.udp.proto.DefaultPacket;
import com.irving.udp.proto.DefaultPacket.Packet;
import com.irving.udp.proto.pojo.UDPPacket;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * UDP 连接握手认证
 * @author yuanyc
 */
public class LoingAuthReqHandler extends ChannelHandlerAdapter{
	
	/**
	 * UDP协议启动后，channelActive会被调用（没有握手）
	 * 直接在此处发送建链请求包
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		DefaultHeader.Header.Builder headBuilder = DefaultHeader.Header.newBuilder();
		headBuilder.setPt(ProtoType.AUTH);
		headBuilder.setSn(IDGenerator.nextId());
		Header header = headBuilder.build();
		
		DefaultPacket.Packet.Builder packetBuilder = DefaultPacket.Packet.newBuilder();
		packetBuilder.setHeader(header);
		Packet packet = packetBuilder.build();
		
		UDPPacket udpPacket = new UDPPacket();
		udpPacket.setContent(packet);
		
		super.channelActive(ctx);
	}
	
}
