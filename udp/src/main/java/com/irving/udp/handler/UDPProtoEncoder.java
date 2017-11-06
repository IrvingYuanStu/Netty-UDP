package com.irving.udp.handler;

import static io.netty.buffer.Unpooled.wrappedBuffer;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLite.Builder;
import com.irving.udp.proto.pojo.UDPPacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * UDProto.msg(protobuf --> bytebuf)
 */
public class UDPProtoEncoder extends MessageToMessageEncoder<UDPPacket>{

	/**
	 * protobuf 编码 
	 * UDPPacket.content pojo-->byte
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, UDPPacket udpPacket, List<Object> out) throws Exception {
        if (udpPacket.getContent() instanceof MessageLite) {
        	MessageLite msg = (MessageLite) udpPacket.getContent();
        	udpPacket.setContent(wrappedBuffer(msg.toByteArray()));
        	out.add(udpPacket);
            return;
        }
        if (udpPacket instanceof MessageLite.Builder) {
        	MessageLite.Builder msgBuilder = (Builder) udpPacket.getContent();
        	udpPacket.setContent(wrappedBuffer(msgBuilder.build().toByteArray()));
            out.add(out.add(udpPacket));
        }
	}

}
