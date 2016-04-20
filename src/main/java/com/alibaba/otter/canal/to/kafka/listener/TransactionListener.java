package com.alibaba.otter.canal.to.kafka.listener;

import com.alibaba.fastjson.JSONObject;

/**
 * cdc数据监听端口
 * 
 * @author bixy
 * 
 */
public interface TransactionListener {

	public void pocess(JSONObject jsonObject);
}
