package com.alibaba.otter.canal.to.kafka;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.Header;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.to.kafka.embed.ToKafkaEmbedSelector;
import com.alibaba.otter.canal.to.kafka.listener.TransactionListener;

/**
 * 负责分析变更数据并且推向kafka
 * 
 * @author bixy
 * 
 */
public class TransactionNotifier extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(TransactionNotifier.class);
	private TransactionListener listener;
	private JSONObject jsonObject;
	private volatile boolean inTransaction = false;
	private volatile boolean running = false;
	private ToKafkaEmbedSelector toKafkaEmbedSelector;

	public TransactionNotifier(String destination, TransactionListener listener, SpringCanalInstanceGeneratorWraper springCanalInstanceGeneratorWraper) {
		this.listener = listener;
		this.toKafkaEmbedSelector = new ToKafkaEmbedSelector(destination, springCanalInstanceGeneratorWraper);
	}

	@Override
	public void run() {
		toKafkaEmbedSelector.start();
		running = true;
		while (running) {
			long batchId = 0L;
			try {
				while (running) {
					Message message = toKafkaEmbedSelector.selector(); // 获取指定数量的数据
					batchId = message.getId();
					int size = message.getEntries().size();
					if (batchId == -1 || size == 0) {
						continue;
					} else {
						analysisEntry(message.getEntries());
					}
					// toKafkaEmbedSelector.ack(batchId);// 提交确认
				}
			} catch (Exception e) {
				logger.error("", e);
				e.printStackTrace();
				toKafkaEmbedSelector.rollback(batchId);// 处理失败, 回滚数据
				logger.error("process error!", e);
			}
		}
		toKafkaEmbedSelector.stop();
	}

	public void stopNotifier() {
		logger.info("stop notifier");
		this.running = false;
	}

	public void analysisEntry(List<Entry> entrys) {
		for (Entry entry : entrys) {
			logger.info("EntryType is ............." + entry.getEntryType());
			if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN) {
				if (!this.inTransaction) {
					this.inTransaction = true;
					jsonObject = new JSONObject();
				} else
					throw new RuntimeException("transaction must end before it's start!!!");
			}
			if (entry.getEntryType() == EntryType.TRANSACTIONEND) {
				if (this.inTransaction) {
					this.inTransaction = false;

					// 关键负责处理数据的listener
					listener.pocess(jsonObject);
					jsonObject = null;
				} else {
					// 直接略过
					// throw new
					// RuntimeException("transcation must start before it's end!!!");
				}
			}
			if (entry.getEntryType() == EntryType.ROWDATA) {
				if (jsonObject == null)
					jsonObject = new JSONObject();
				JSONObject header = jsonObject.getJSONObject("header");
				if (header == null) {
					header = new JSONObject();
					Header entryHeader = entry.getHeader();
					header.put("logfileName", entryHeader.getLogfileName());
					header.put("logfileOffset", entryHeader.getLogfileOffset());
					header.put("serverId", entryHeader.getServerId());
					header.put("serverEncode", entryHeader.getServerenCode());
					header.put("executeTime", entryHeader.getExecuteTime());
					header.put("sourceType", entryHeader.getSourceType());
					header.put("schemaName", entryHeader.getSchemaName());
					header.put("tableName", entryHeader.getTableName());
					header.put("eventLength", entryHeader.getEventLength());
					// header.put("eventType", entryHeader.getEventType());
					jsonObject.put("header", header);
				}
				RowChange rowChange = null;
				try {
					rowChange = RowChange.parseFrom(entry.getStoreValue());
				} catch (Exception e) {
					throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
				}
				JSONArray rowChangeArray = jsonObject.getJSONArray("rowChange");
				if (rowChangeArray == null) {
					rowChangeArray = new JSONArray();
					jsonObject.put("rowChange", rowChangeArray);
				}
				JSONObject rowChangeObject = new JSONObject();
				rowChangeObject.put("tableId", rowChange.getTableId());
				rowChangeObject.put("eventType", rowChange.getEventType());
				rowChangeObject.put("isDdl", rowChange.getIsDdl());
				rowChangeObject.put("sql", rowChange.getSql());
				rowChangeObject.put("ddlSchemaName", rowChange.getDdlSchemaName());
				JSONObject rowDataObject = new JSONObject();
				rowChangeObject.put("rowData", rowDataObject);
				EventType eventType = rowChange.getEventType();
				for (RowData rowData : rowChange.getRowDatasList()) {
					if (eventType == EventType.DELETE) {
						rowDataObject.put("beforeColumn", toColums(rowData.getBeforeColumnsList()));
					} else if (eventType == EventType.INSERT) {
						rowDataObject.put("afterColumn", toColums(rowData.getAfterColumnsList()));
					} else {
						rowDataObject.put("beforeColumn", toColums(rowData.getBeforeColumnsList()));
						rowDataObject.put("afterColumn", toColums(rowData.getAfterColumnsList()));
					}
				}
				rowChangeArray.add(rowChangeObject);
			}
		}
	}

	private JSONArray toColums(List<Column> columns) {
		JSONArray array = new JSONArray();
		for (Column col : columns) {
			JSONObject jo = new JSONObject();
			jo.put("index", col.getIndex());
			// jo.put("sqlType", col.getSqlType());
			jo.put("name", col.getName());
			jo.put("isKey", col.getIsKey());
			jo.put("updated", col.getUpdated());
			jo.put("isNull", col.getIsNull());
			jo.put("value", col.getValue());
			jo.put("length", col.getLength());
			jo.put("mysqlType", col.getMysqlType());
			array.add(jo);
		}
		return array;
	}

	public void setListener(TransactionListener listener) {
		this.listener = listener;
	}

}
