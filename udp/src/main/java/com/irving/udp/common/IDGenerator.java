package com.irving.udp.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程安全的ID生成器
 * @author yuanyc
 */
public class IDGenerator {
	
	/**
	 * AtomicLong 无锁的线程安全原子性自增
	 * 通过CAS算法实现
	 */
	private static AtomicLong count = new AtomicLong(1L);

	public static long nextId() {
		return count.incrementAndGet();
	}
}
