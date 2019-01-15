package com.irving.tcp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/14 2:17 PM
 **/
public class Decoder extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        if (byteBuf.readableBytes() < 4) {  //小于4字节
            return; //不处理buf,继续等待
        }

        byte[] recv = new byte[200];    //假设头部200字节
        // 假设再从头部取得数据区长度
        int readerIndex = byteBuf.readerIndex();
        int datalen = byteBuf.getInt(readerIndex);
        if (byteBuf.readableBytes() < 4 + datalen) {
            return; //读取dl后，再计算一次，判断是否还有数据区是否为0
        }
        // 读取后续的所有数据
    }
}
