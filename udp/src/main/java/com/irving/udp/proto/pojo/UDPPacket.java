package com.irving.udp.proto.pojo;

/**
 * 自定义UDP数据包POJO </br>
 * @version 0.1.0
 * @author yuanyc
 */
public class UDPPacket {
	
	private String src; // 源地址 ip:port
	private String dst; // 目的地址 
	private Object content;	// 包裹自定义协议（可以是Byte，也可以是POJO）
	
	public UDPPacket() {}
	
	public UDPPacket(String dst, Object content) {
		this.dst = dst;
		this.content = content;
	}
	
	public UDPPacket(String src, String dst, Object content) {
		this.src = src;
		this.dst = dst;
		this.content = content;
	}
	
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}

}
