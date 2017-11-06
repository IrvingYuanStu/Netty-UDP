package com.irving.udp.handler;

import java.util.List;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageLite;
import com.irving.udp.proto.UDPPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 基于UDP的Protobuf解码器
 * （MessageToMessageDecoder<UDPPacket> 从java对象转换为另一种POJO对象）
 * @author yuanyc
 */
public class UDPProtoDecoder extends MessageToMessageDecoder<UDPPacket>{
	
	 private static final boolean HAS_PARSER;

	    static {
	        boolean hasParser = false;
	        try {
	            // MessageLite.getParsetForType() is not available until protobuf 2.5.0.
	            MessageLite.class.getDeclaredMethod("getParserForType");
	            hasParser = true;
	        } catch (Throwable t) {
	            // Ignore
	        }

	        HAS_PARSER = hasParser;
	    }

	    private final MessageLite prototype;
	    private final ExtensionRegistry extensionRegistry;
	    
	    public UDPProtoDecoder(MessageLite prototype) {
	        this(prototype, null);
	    }

	    public UDPProtoDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
	        if (prototype == null) {
	            throw new NullPointerException("prototype");
	        }
	        this.prototype = prototype.getDefaultInstanceForType();
	        this.extensionRegistry = extensionRegistry;
	    }

	/**
	 * 解码 </br>
	 * udpPacket.content byte-->pojo
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, UDPPacket udpPacket, List<Object> out) throws Exception {
		// UDPPacket.msg此时是ByteBuf类型
		ByteBuf msg = (ByteBuf) udpPacket.getContent();
		final byte[] array;
        final int offset;
        final int length = msg.readableBytes();
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = new byte[length];
            msg.getBytes(msg.readerIndex(), array, 0, length);
            offset = 0;
        }

        if (extensionRegistry == null) {
            if (HAS_PARSER) {
            	udpPacket.setContent(prototype.getParserForType().parseFrom(array, offset, length));
            } else {
            	udpPacket.setContent(prototype.newBuilderForType().mergeFrom(array, offset, length).build());
            }
        } else {
            if (HAS_PARSER) {
            	udpPacket.setContent(prototype.getParserForType().parseFrom(array, offset, length, extensionRegistry));
            } else {
            	udpPacket.setContent(prototype.newBuilderForType().mergeFrom(array, offset, length, extensionRegistry).build());
            }
        }
        out.add(udpPacket);
	}

}
