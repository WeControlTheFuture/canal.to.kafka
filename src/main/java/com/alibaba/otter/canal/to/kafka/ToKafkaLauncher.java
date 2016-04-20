package com.alibaba.otter.canal.to.kafka;

import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.CanalLifeCycle;
import com.alibaba.otter.canal.to.kafka.client.ToKafkaClientController;
import com.alibaba.otter.canal.to.kafka.embed.ToKafkaEmbedController;

/**
 * mysql变更数据发往kafka入口
 * 
 * @author bixy
 * 
 */
public class ToKafkaLauncher {
	private final static Logger logger = LoggerFactory.getLogger(ToKafkaLauncher.class);
	private static CanalLifeCycle controller;
	private static String style = "embed";

	private static void printUsage() {
		System.out.println("Usage: ToKafkaLauncher [OPTION]... [RUNSTYLE]...");
		System.out.println("-runStyle	style to run cluster simple embed.");
	}

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				logger.warn("you need choose a style to run,if you don't default is embed........args.length is " + args.length);
				printUsage();
			} else {
				CommandLineParser parser = new PosixParser();
				Options options = new Options();
				options.addOption("runStyle", true, "to kafka style to run.");
				CommandLine line = parser.parse(options, args);
				if (line.hasOption("runStyle")) {
					style = line.getOptionValue("runStyle");
				}
			}
			CanalConnector connector = null;
			Properties properties = new Properties();
			properties.load(ToKafkaLauncher.class.getClassLoader().getResourceAsStream("canal-to-kafka.properties"));
			String destination = properties.getProperty("canal.instance.destination");
//			logger.warn("start kafka launcher with style .........." + style);
			if (style.equalsIgnoreCase("cluster")) {
				// 基于zookeeper动态获取canal
				// server的地址，建立链接，其中一台server发生crash，可以支持failover
				String zkServers = properties.getProperty("canal.zkServers");
				connector = CanalConnectors.newClusterConnector(zkServers, destination, "", "");
			} else if (style.equalsIgnoreCase("simple")) {
				// 根据ip，直接创建链接，无HA的功能
				InetSocketAddress address = new InetSocketAddress(properties.getProperty("canal.ip"), Integer.parseInt(properties.getProperty("canal.port")));
				connector = CanalConnectors.newSingleConnector(address, destination, "", "");
				controller = new ToKafkaClientController(connector, destination);
				controller.start();
			} else if (style.equals("embed")) {
				controller = new ToKafkaEmbedController(properties);
				controller.start();
				logger.info("## canal-to-kafka process started!");
			}
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						logger.info("## stop the to-kafka process");
						controller.stop();
					} catch (Throwable e) {
						logger.warn("##something goes wrong when stopping to-kafka process:\n{}", ExceptionUtils.getFullStackTrace(e));
					} finally {
						logger.info("## to-kafka process is down.");
					}
				}
			});
		} catch (Exception e) {
			logger.error("## Something goes wrong when starting up the ToKafkaClient:\n{}", ExceptionUtils.getFullStackTrace(e));
			System.exit(0);
		}
	}

}
