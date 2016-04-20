package com.alibaba.otter.canal.to.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.otter.canal.deployer.InstanceConfig;
import com.alibaba.otter.canal.deployer.monitor.InstanceAction;
import com.alibaba.otter.canal.to.kafka.listener.impl.ToKafkaListener;
import com.google.common.collect.MapMaker;

/**
 * 配置扫描触发动作
 * 
 * @author bixy
 * 
 */
public class ConfigScanAction implements InstanceAction {
	protected final static Logger logger = LoggerFactory.getLogger(ConfigScanAction.class);
	private InstanceConfig globalInstanceConfig;
	private Map<String, InstanceConfig> instanceConfigs;
	private Map<String, TransactionNotifier> toKafkaThreadMap;
	private SpringCanalInstanceGeneratorWraper springCanalInstanceGeneratorWraper;
	private Properties properties;

	public ConfigScanAction(InstanceConfig globalInstanceConfig, Properties properties, SpringCanalInstanceGeneratorWraper springCanalInstanceGeneratorWraper) {
		this.globalInstanceConfig = globalInstanceConfig;
		this.properties = properties;
		this.instanceConfigs = new HashMap<String, InstanceConfig>();
		this.toKafkaThreadMap = new HashMap<String, TransactionNotifier>();
		this.springCanalInstanceGeneratorWraper = springCanalInstanceGeneratorWraper;
	}

	@Override
	public synchronized void start(String destination) {
		try {
			logger.info("start destination............." + destination + "----------------------" + this);
			InstanceConfig config = instanceConfigs.get(destination);
			logger.info("config========================="+config);
			if (config == null) {
				// 重新读取一下instance config
				// config = ConfigUtils.parseInstanceConfig(globalInstanceConfig,
				// properties, destination);
				instanceConfigs.put(destination, globalInstanceConfig);
				MDSInfo mdsInfo = springCanalInstanceGeneratorWraper.getMDSInfo(destination);
				TransactionNotifier transactionNotifier = new TransactionNotifier(destination, new ToKafkaListener(destination, mdsInfo), springCanalInstanceGeneratorWraper);
				transactionNotifier.start();
				toKafkaThreadMap.put(destination, transactionNotifier);
			}
			logger.info("toKafkaThreadMap.size====================" + toKafkaThreadMap.size());
		} catch (Exception e) {
			logger.error("",e);
		}
		// if (!config.getLazy() && !embededCanalServer.isStart(destination)) {
		// // HA机制启动
		// ServerRunningMonitor runningMonitor =
		// ServerRunningMonitors.getRunningMonitor(destination);
		// if (!runningMonitor.isStart()) {
		// runningMonitor.start();
		// }
		// }
	}

	@Override
	public void stop(String destination) {
		try {
			logger.info("stop destination............." + destination + "----------------------" + this);
			logger.info("toKafkaThreadMap.size====================" + toKafkaThreadMap.size());
			for (Entry<String, TransactionNotifier> entry : toKafkaThreadMap.entrySet())
				logger.info("destination--------------" + entry.getKey() + "-----------------" + entry.getValue());
			// 此处的stop，代表强制退出，非HA机制，所以需要退出HA的monitor和配置信息
			TransactionNotifier transactionNotifier = toKafkaThreadMap.get(destination);
			transactionNotifier.stopNotifier();
			toKafkaThreadMap.remove(destination);
			instanceConfigs.remove(destination);
			// InstanceConfig config = instanceConfigs.remove(destination);
			// if (config != null) {
			// embededCanalServer.stop(destination);
			// ServerRunningMonitor runningMonitor =
			// ServerRunningMonitors.getRunningMonitor(destination);
			// if (runningMonitor.isStart()) {
			// runningMonitor.stop();
			// }
			// }
		} catch (Exception e) {
			logger.error("stop exception", e);
		}
	}

	@Override
	public void reload(String destination) {
		// 目前任何配置变化，直接重启，简单处理
		try {
			stop(destination);
			start(destination);
		} catch (Exception e) {
			logger.error("reload exception", e);
		}
	}

}
