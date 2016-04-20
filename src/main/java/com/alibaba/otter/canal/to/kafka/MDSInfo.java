package com.alibaba.otter.canal.to.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;




import org.apache.commons.lang.StringUtils;

import com.alibaba.otter.canal.filter.aviater.AviaterRegexFilter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 调用mds方法所需要的一些配置
 * 
 * @author bixy
 * 
 */
public class MDSInfo {
	private String authAddress;
	private String serviceId;
	private String user;
	private String servicePassword;
	private String topic;
	private Integer partition;
	private String tableFilterRule;
	private Map<AviaterRegexFilter, String> tableRule;

	public void init() {
		if (StringUtils.isNotEmpty(tableFilterRule)) {
			tableRule = new HashMap<AviaterRegexFilter, String>();
			JsonObject jo = new Gson().fromJson(tableFilterRule, JsonObject.class);
			for (Entry<String, JsonElement> entry : jo.entrySet()) {
				tableRule.put(new AviaterRegexFilter(entry.getKey()), entry.getValue().getAsString());
			}
		}
	}

	public String resetTableName(String table) {
		for (Entry<AviaterRegexFilter, String> entry : tableRule.entrySet()) {
			if (entry.getKey().filter(table)) {
				return entry.getValue();
			}
		}
		return table;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getServicePassword() {
		return servicePassword;
	}

	public void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Integer getPartition() {
		return partition;
	}

	public void setPartition(Integer partition) {
		this.partition = partition;
	}

	public String getTableFilterRule() {
		return tableFilterRule;
	}

	public void setTableFilterRule(String tableFilterRule) {
		this.tableFilterRule = tableFilterRule;
	}

	public String getAuthAddress() {
		return authAddress;
	}

	public void setAuthAddress(String authAddress) {
		this.authAddress = authAddress;
	}

	public Map<AviaterRegexFilter, String> getTableRule() {
		return tableRule;
	}

	public void setTableRule(Map<AviaterRegexFilter, String> tableRule) {
		this.tableRule = tableRule;
	}

}
