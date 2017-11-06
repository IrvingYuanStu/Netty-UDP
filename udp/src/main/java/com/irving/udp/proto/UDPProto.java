package com.irving.udp.proto;

import java.net.InetSocketAddress;

/**
 * UDP协议数据包对应实体类
 * @author yuanyc
 */
public class UDPProto {
	// 源地址
	private InetSocketAddress inetSocketAddress;
	// 数据端内容（object类型便于从Buff转换为第一POJO）
	private Object msg;
	
	public UDPProto(){}
	
	public UDPProto(InetSocketAddress inetSocketAddress, Object msg){
		this.inetSocketAddress = inetSocketAddress;
		this.msg = msg;
	}
	
	public InetSocketAddress getInetSocketAddress() {
		return inetSocketAddress;
	}
	public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
		this.inetSocketAddress = inetSocketAddress;
	}
	public Object getMsg() {
		return msg;
	}
	public void setMsg(Object msg) {
		this.msg = msg;
	}
}
