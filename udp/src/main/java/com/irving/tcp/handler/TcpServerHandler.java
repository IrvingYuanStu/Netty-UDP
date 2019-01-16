package com.irving.tcp.handler;

import com.irving.tcp.bean.SignUpReq;
import com.irving.tcp.bean.SignUpResp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description
 * @Author yuanyc
 * @Date 2019/1/11 5:38 PM
 **/
public class TcpServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接了一个");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        handleBuffer(msg);
        SignUpResp resp = this.handleObject(msg);
        ctx.writeAndFlush(resp);
    }

    /**
     * 处理buffer数据
     * @Author yuanyc
     * @Date 11:30 AM 2019/1/16
     **/
    private void handleBuffer(Object msg) {
        // 相当于jdk ByteBuffer
        ByteBuf byteBuf = (ByteBuf) msg;
        // 全部读取
        byte[] req = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(req);

        String str = new String(req);
        System.out.println(str);
    }

    /**
     * 处理Java序列化
     * @Author yuanyc
     * @Date 11:38 AM 2019/1/16
     * @Param
     * @Return com.irving.tcp.bean.SignUpResp
     **/
    private SignUpResp handleObject(Object msg) {
        SignUpReq req = (SignUpReq) msg;
        System.out.println("recv obj = " + req);

        SignUpResp resp = new SignUpResp();
        resp.setReqId(req.getReqId());
        resp.setStatusCode(200);
        resp.setResultMsg("成功");
        return resp;
    }

}
