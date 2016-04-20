package com.alibaba.otter.canal.to.kafka.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.Assert;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.common.CanalLifeCycle;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.to.kafka.TransactionNotifier;
import com.alibaba.otter.canal.to.kafka.listener.impl.ToKafkaListener;

public class ToKafkaClientController implements CanalLifeCycle {
	protected final static Logger logger = LoggerFactory.getLogger(ToKafkaClientController.class);
	private CanalConnector connector;
	protected Thread thread = null;
	private String destination;
	private TransactionNotifier transactionNotifier;
	protected volatile boolean running = false;
	protected Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("parse events has an error", e);
		}
	};

	public ToKafkaClientController(CanalConnector connector, String destination) {
		this.connector = connector;
		this.destination = destination;
	}

	public void start() {
		Assert.notNull(connector, "connector is null");
		transactionNotifier = new TransactionNotifier(destination, new ToKafkaListener("", null), null);
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int batchSize = 5 * 1024;
				while (running) {
					try {
						MDC.put("destination", destination);
						connector.connect();
						connector.subscribe();
						while (running) {
							Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
							long batchId = message.getId();
							int size = message.getEntries().size();
							if (batchId == -1 || size == 0) {
							} else {
								transactionNotifier.analysisEntry(message.getEntries());
							}

							connector.ack(batchId); // 提交确认
							// connector.rollback(batchId); // 处理失败, 回滚数据
						}
					} catch (Exception e) {
						logger.error("process error!", e);
					} finally {
						connector.disconnect();
						MDC.remove("destination");
					}
				}
			}
		});
		thread.setUncaughtExceptionHandler(handler);
		this.running = true;
		thread.start();
	}

	public void stop() {
		if (!this.running) {
			return;
		}
		this.running = false;
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// ignore
			}
		}
		MDC.remove("destination");
	}

	public void setConnector(CanalConnector connector) {
		this.connector = connector;
	}

	@Override
	public boolean isStart() {
		return running;
	}
}
