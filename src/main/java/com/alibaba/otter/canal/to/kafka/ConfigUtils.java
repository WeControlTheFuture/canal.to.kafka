package com.alibaba.otter.canal.to.kafka;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.alibaba.otter.canal.deployer.CanalConstants;
import com.alibaba.otter.canal.deployer.InstanceConfig;
import com.alibaba.otter.canal.deployer.InstanceConfig.InstanceMode;

/**
 * 配置工具
 * 
 * @author bixy
 * 
 */
public class ConfigUtils {
	/**
	 * 初始化全局配置
	 * 
	 * @param properties
	 * @return
	 */
	public static InstanceConfig initGlobalConfig(Properties properties) {
		InstanceConfig globalConfig = new InstanceConfig();
		String modeStr = ConfigUtils.getProperty(properties, CanalConstants.getInstanceModeKey(CanalConstants.GLOBAL_NAME));
		if (StringUtils.isNotEmpty(modeStr)) {
			globalConfig.setMode(InstanceMode.valueOf(StringUtils.upperCase(modeStr)));
		}
		String lazyStr = ConfigUtils.getProperty(properties, CanalConstants.getInstancLazyKey(CanalConstants.GLOBAL_NAME));
		if (StringUtils.isNotEmpty(lazyStr)) {
			globalConfig.setLazy(Boolean.valueOf(lazyStr));
		}
		String managerAddress = ConfigUtils.getProperty(properties, CanalConstants.getInstanceManagerAddressKey(CanalConstants.GLOBAL_NAME));
		if (StringUtils.isNotEmpty(managerAddress)) {
			globalConfig.setManagerAddress(managerAddress);
		}
		String springXml = ConfigUtils.getProperty(properties, CanalConstants.getInstancSpringXmlKey(CanalConstants.GLOBAL_NAME));
		if (StringUtils.isNotEmpty(springXml)) {
			globalConfig.setSpringXml(springXml);
		}
		return globalConfig;
	}

	public static InstanceConfig parseInstanceConfig(InstanceConfig globalInstanceConfig, Properties properties, String destination) {
		InstanceConfig config = new InstanceConfig(globalInstanceConfig);
		String modeStr = getProperty(properties, CanalConstants.getInstanceModeKey(destination));
		if (!StringUtils.isEmpty(modeStr)) {
			config.setMode(InstanceMode.valueOf(StringUtils.upperCase(modeStr)));
		}

		String lazyStr = getProperty(properties, CanalConstants.getInstancLazyKey(destination));
		if (!StringUtils.isEmpty(lazyStr)) {
			config.setLazy(Boolean.valueOf(lazyStr));
		}

		if (config.getMode().isManager()) {
			String managerAddress = getProperty(properties, CanalConstants.getInstanceManagerAddressKey(destination));
			if (StringUtils.isNotEmpty(managerAddress)) {
				config.setManagerAddress(managerAddress);
			}
		} else if (config.getMode().isSpring()) {
			String springXml = getProperty(properties, CanalConstants.getInstancSpringXmlKey(destination));
			if (StringUtils.isNotEmpty(springXml)) {
				config.setSpringXml(springXml);
			}
		}

		return config;
	}

	public static String getProperty(Properties properties, String key) {
		return StringUtils.trim(properties.getProperty(StringUtils.trim(key)));
	}
}
