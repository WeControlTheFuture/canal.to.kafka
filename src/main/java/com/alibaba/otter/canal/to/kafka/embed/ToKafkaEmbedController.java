package com.alibaba.otter.canal.to.kafka.embed;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.otter.canal.common.CanalLifeCycle;
import com.alibaba.otter.canal.deployer.CanalConstants;
import com.alibaba.otter.canal.deployer.InstanceConfig;
import com.alibaba.otter.canal.deployer.monitor.SpringInstanceConfigMonitor;
import com.alibaba.otter.canal.to.kafka.ConfigScanAction;
import com.alibaba.otter.canal.to.kafka.ConfigUtils;
import com.alibaba.otter.canal.to.kafka.SpringCanalInstanceGeneratorWraper;

/**
 * 启动控制ToKafkaEmbedSelector
 * 
 * @author bixy
 * 
 */
public class ToKafkaEmbedController extends Thread implements CanalLifeCycle {
	protected final static Logger logger = LoggerFactory.getLogger(ToKafkaEmbedController.class);
	private SpringInstanceConfigMonitor instanceConfigMonitor;
	private InstanceConfig globalInstanceConfig;
	private Properties properties;
	private volatile boolean running = false;
	private SpringCanalInstanceGeneratorWraper springCanalInstanceGeneratorWraper = new SpringCanalInstanceGeneratorWraper();
	protected Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("parse events has an error", e);
		}
	};

	public ToKafkaEmbedController(Properties properties) {
		this.properties = properties;
	}

	@Override
	public synchronized void start() {
		// 初始化全局参数设置
		globalInstanceConfig = ConfigUtils.initGlobalConfig(properties);
		// 初始化配置变更的监控
		generateMonitor();
		this.running = true;
		super.start();
	}

	public void run() {
		while (running) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				logger.error("ToKafkaEmbedController run exception", e);
			}
		}
	}

	/**
	 * 生成配置监控程序，扫描目录新增实例
	 */
	private void generateMonitor() {
		instanceConfigMonitor = new SpringInstanceConfigMonitor();
		instanceConfigMonitor.setScanIntervalInSecond(1);
		instanceConfigMonitor.setDefaultAction(new ConfigScanAction(globalInstanceConfig, properties, springCanalInstanceGeneratorWraper));
		// 设置conf目录，默认是user.dir + conf目录组成
		String rootDir = ConfigUtils.getProperty(properties, CanalConstants.CANAL_CONF_DIR);
		logger.info("## config monitor dir is " + rootDir);
		if (StringUtils.isEmpty(rootDir)) {
			rootDir = "E:/instances";
		}
		instanceConfigMonitor.setRootConf(rootDir);
		instanceConfigMonitor.start();
	}

	@Override
	public boolean isStart() {
		return running;
	}

}
