package com.alibaba.otter.canal.to.kafka.embed;

import java.util.concurrent.locks.LockSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.otter.canal.protocol.ClientIdentity;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.server.embeded.CanalServerWithEmbeded;
import com.alibaba.otter.canal.to.kafka.SpringCanalInstanceGeneratorWraper;

/**
 * 捆绑CanalServerWithEmbeded的数据源
 * 
 * @author bixy
 * 
 */
public class ToKafkaEmbedSelector {
	private static final Logger logger = LoggerFactory.getLogger(ToKafkaEmbedSelector.class);
	private static final int maxEmptyTimes = 10;

	private CanalServerWithEmbeded canalServer;
	private volatile boolean running = false;

	private ClientIdentity clientIdentity;

	private String destination;
	private String filter;
	private int batchSize = 10000;
	private short slaveId;
	private SpringCanalInstanceGeneratorWraper springCanalInstanceGeneratorWraper;

	public ToKafkaEmbedSelector(String destination, SpringCanalInstanceGeneratorWraper springCanalInstanceGeneratorWraper) {
		this.canalServer = new CanalServerWithEmbeded();
		this.destination = destination;
		this.springCanalInstanceGeneratorWraper = springCanalInstanceGeneratorWraper;
	}

	public void start() {
		if (running) {
			return;
		}
		canalServer.setCanalInstanceGenerator(springCanalInstanceGeneratorWraper);
		canalServer.start();
		canalServer.start(destination);
		this.clientIdentity = new ClientIdentity(destination, slaveId, filter);
		canalServer.subscribe(clientIdentity);// 发起一次订阅
		running = true;
	}
	
	public void stop() {
		canalServer.stop();
		canalServer.stop(destination);
		canalServer.unsubscribe(clientIdentity);
		running = false;
	}

	public Message selector() throws InterruptedException {
		int emptyTimes = 0;
		Message message = null;
		while (running) {
			message = canalServer.get(clientIdentity, batchSize);
			if (message == null || message.getId() == -1L) { // 代表没数据
				applyWait(emptyTimes++);
			} else {
				break;
			}
		}
		if (!running) {
			throw new InterruptedException();
		}
		return message;
	}

	public void ack(long batchId) {
		canalServer.ack(clientIdentity, batchId);
	}

	public void rollback(long batchId) {
		logger.error("roll back................." + batchId);
		canalServer.rollback(clientIdentity, batchId);
	}

	// 处理无数据的情况，避免空循环挂死
	private void applyWait(int emptyTimes) {
		int newEmptyTimes = emptyTimes > maxEmptyTimes ? maxEmptyTimes : emptyTimes;
		if (emptyTimes <= 3) { // 3次以内
			Thread.yield();
		} else { // 超过3次，最多只sleep 10ms
			LockSupport.parkNanos(1000 * 1000L * newEmptyTimes);
		}
	}

	public void setSlaveId(short slaveId) {
		this.slaveId = slaveId;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

}
