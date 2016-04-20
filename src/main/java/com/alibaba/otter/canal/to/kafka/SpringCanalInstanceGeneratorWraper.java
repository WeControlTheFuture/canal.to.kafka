package com.alibaba.otter.canal.to.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.otter.canal.common.CanalException;
import com.alibaba.otter.canal.deployer.CanalConstants;
import com.alibaba.otter.canal.instance.core.CanalInstance;
import com.alibaba.otter.canal.instance.core.CanalInstanceGenerator;
import com.alibaba.otter.canal.instance.spring.SpringCanalInstanceGenerator;

/**
 * 生成spring cannal 实例的包装器
 * 
 * @author bixy
 * 
 */
public class SpringCanalInstanceGeneratorWraper implements CanalInstanceGenerator {
	private final static Logger logger = LoggerFactory.getLogger(SpringCanalInstanceGeneratorWraper.class);
	private SpringCanalInstanceGenerator instanceGenerator = new SpringCanalInstanceGenerator();

	@Override
	public synchronized CanalInstance generate(String destination) {
		try {
			logger.info("load ..............................................." + destination);
			// 设置当前正在加载的通道，加载spring查找文件时会用到该变量
			System.setProperty(CanalConstants.CANAL_DESTINATION_PROPERTY, destination);
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("canal-instance.xml");
			instanceGenerator.setBeanFactory(context);
			return instanceGenerator.generate(destination);
		} catch (Throwable e) {
			logger.error("generator instance failed.", e);
			throw new CanalException(e);
		} finally {
			System.setProperty(CanalConstants.CANAL_DESTINATION_PROPERTY, "");
		}
	}

	public synchronized MDSInfo getMDSInfo(String destination) {
		// 设置当前正在加载的通道，加载spring查找文件时会用到该变量
		System.setProperty(CanalConstants.CANAL_DESTINATION_PROPERTY, destination);
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("canal-instance.xml");
		return (MDSInfo) context.getBean("mdsInfo");
	}
}
