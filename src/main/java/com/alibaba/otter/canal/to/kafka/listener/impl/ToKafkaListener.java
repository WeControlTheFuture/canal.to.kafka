package com.alibaba.otter.canal.to.kafka.listener.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.mds.IMessageSender;
import com.ai.paas.ipaas.mds.MsgSenderFactory;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.to.kafka.MDSInfo;
import com.alibaba.otter.canal.to.kafka.listener.TransactionListener;

public class ToKafkaListener implements TransactionListener {
	private final static Logger logger = LoggerFactory.getLogger(ToKafkaListener.class);
	private String destination;
	private MDSInfo mdsInfo;
	private IMessageSender msgSender;

	public ToKafkaListener(String destination, MDSInfo mdsInfo) {
		this.destination = destination;
		this.mdsInfo = mdsInfo;
		this.mdsInfo.init();
		AuthDescriptor ad = new AuthDescriptor(mdsInfo.getAuthAddress(), mdsInfo.getUser(), mdsInfo.getServicePassword(), mdsInfo.getServiceId());
		msgSender = MsgSenderFactory.getClient(ad, mdsInfo.getTopic());
	}

	@Override
	public void pocess(JSONObject jsonObject) {
		logger.info(destination + " send message to kafka {}", jsonObject.toString());
		JSONObject head = jsonObject.getJSONObject("header");
		if (head != null) {
			String table = head.getString("tableName");
			String talbeFiltered = mdsInfo.resetTableName(table);
			jsonObject.getJSONObject("header").put("tableName", talbeFiltered);
			msgSender.send(jsonObject.toString(), Math.abs(talbeFiltered.hashCode() % mdsInfo.getPartition()));
		}
	}
}
