package com.irving.udp.common.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * 链接管理器
 * @author yuanyc
 */
public class ChannelManager {

	/**
	 * 玩家IP与channel的关系
	 */
	private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
	
	private static final ChannelManager INSTANCE = new ChannelManager();
	
	private ChannelManager() {}
	
	public static ChannelManager getInstance() {
		return INSTANCE;
	}
	
	public Channel getChannel(String src) {
		return channelMap.get(src);
	}
	
	public void setChannel(String src, Channel channel) {
		channelMap.put(src, channel);
	}
	
	public void removeChannel(String src) {
		channelMap.remove(src);
	}
	
	public void removeChannel(Channel channel) {
		Iterator<Map.Entry<String, Channel>> it = channelMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Channel> entry = it.next();
			Channel old = entry.getValue();
			if (old.id().equals(channel.id())) {
				it.remove();
			}
		}
	}
	
}
