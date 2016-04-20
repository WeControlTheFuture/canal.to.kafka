package com.alibaba.otter.canal.to.kafka;

import com.google.gson.Gson;

public class DesMsg {
	private Head header;
	private RowChange[] rowChange;

	public Head getHeader() {
		return header;
	}

	public void setHeader(Head header) {
		this.header = header;
	}

	public RowChange[] getRowChange() {
		return rowChange;
	}

	public void setRowChange(RowChange[] rowChange) {
		this.rowChange = rowChange;
	}

	public static class Head {
		private Long eventLength;
		private Long executeTime;
		private String logfileName;
		private Long logfileOffset;
		private String schemaName;
		private Long serverId;
		private String serverEncode;
		private String sourceType;
		private String tableName;

		public Long getEventLength() {
			return eventLength;
		}

		public void setEventLength(Long eventLength) {
			this.eventLength = eventLength;
		}

		public Long getExecuteTime() {
			return executeTime;
		}

		public void setExecuteTime(Long executeTime) {
			this.executeTime = executeTime;
		}

		public String getLogfileName() {
			return logfileName;
		}

		public void setLogfileName(String logfileName) {
			this.logfileName = logfileName;
		}

		public Long getLogfileOffset() {
			return logfileOffset;
		}

		public void setLogfileOffset(Long logfileOffset) {
			this.logfileOffset = logfileOffset;
		}

		public String getSchemaName() {
			return schemaName;
		}

		public void setSchemaName(String schemaName) {
			this.schemaName = schemaName;
		}

		public Long getServerId() {
			return serverId;
		}

		public void setServerId(Long serverId) {
			this.serverId = serverId;
		}

		public String getServerEncode() {
			return serverEncode;
		}

		public void setServerEncode(String serverEncode) {
			this.serverEncode = serverEncode;
		}

		public String getSourceType() {
			return sourceType;
		}

		public void setSourceType(String sourceType) {
			this.sourceType = sourceType;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

	}

	public static class RowChange {
		private RowData rowData;
		private String ddlSchemaName;
		private String eventType;
		private Boolean isDdl;
		private String sql;
		private Integer tableId;

		public RowData getRowData() {
			return rowData;
		}

		public void setRowData(RowData rowData) {
			this.rowData = rowData;
		}

		public String getDdlSchemaName() {
			return ddlSchemaName;
		}

		public void setDdlSchemaName(String ddlSchemaName) {
			this.ddlSchemaName = ddlSchemaName;
		}

		public String getEventType() {
			return eventType;
		}

		public void setEventType(String eventType) {
			this.eventType = eventType;
		}

		public Boolean getIsDdl() {
			return isDdl;
		}

		public void setIsDdl(Boolean isDdl) {
			this.isDdl = isDdl;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public Integer getTableId() {
			return tableId;
		}

		public void setTableId(Integer tableId) {
			this.tableId = tableId;
		}

	}

	public static class RowData {
		private Column[] beforeColumn;
		private Column[] afterColumn;

		public Column[] getBeforeColumn() {
			return beforeColumn;
		}

		public void setBeforeColumn(Column[] beforeColumn) {
			this.beforeColumn = beforeColumn;
		}

		public Column[] getAfterColumn() {
			return afterColumn;
		}

		public void setAfterColumn(Column[] afterColumn) {
			this.afterColumn = afterColumn;
		}

	}

	public static class Column {
		private Integer index;
		private Boolean isKey;
		private Boolean isNull;
		private Integer length;
		private String mysqlType;
		private String name;
		private Boolean updated;
		private String value;

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public Boolean getIsKey() {
			return isKey;
		}

		public void setIsKey(Boolean isKey) {
			this.isKey = isKey;
		}

		public Boolean getIsNull() {
			return isNull;
		}

		public void setIsNull(Boolean isNull) {
			this.isNull = isNull;
		}

		public Integer getLength() {
			return length;
		}

		public void setLength(Integer length) {
			this.length = length;
		}

		public String getMysqlType() {
			return mysqlType;
		}

		public void setMysqlType(String mysqlType) {
			this.mysqlType = mysqlType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Boolean getUpdated() {
			return updated;
		}

		public void setUpdated(Boolean updated) {
			this.updated = updated;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public static void main(String[] args) {
		Column col0 = new Column();
		col0.setIndex(0);
		col0.setIsKey(true);
		col0.setIsNull(true);
		col0.setLength(0);
		col0.setMysqlType("int(11)");
		col0.setName("cust_id");
		col0.setUpdated(false);
		col0.setValue("9");
		Column col1 = new Column();
		col1.setIndex(1);
		col1.setIsKey(false);
		col1.setIsNull(false);
		col1.setLength(0);
		col1.setMysqlType("varchar(45)");
		col1.setName("name");
		col1.setUpdated(false);
		col1.setValue("bixy");
		RowData rowData = new RowData();
		rowData.setAfterColumn(new Column[] { col0, col1 });
		RowChange rowChange = new RowChange();
		rowChange.setRowData(rowData);
		rowChange.setDdlSchemaName("");
		rowChange.setEventType("INSERT");
		rowChange.setIsDdl(false);
		rowChange.setSql("");
		rowChange.setTableId(442);
		Head head = new Head();
		head.setEventLength(49L);
		head.setExecuteTime(1440677408000L);
		head.setLogfileName("mysql-bin.000106");
		head.setLogfileOffset(3875882L);
		head.setSchemaName("devrdb12");
		head.setServerId(1L);
		head.setServerEncode("UTF-8");
		head.setSourceType("MYSQL");
		head.setTableName("cust_bixy_00");
		DesMsg msg = new DesMsg();
		msg.setRowChange(new RowChange[] { rowChange });
		msg.setHeader(head);
		System.out.println(new Gson().toJson(msg, DesMsg.class));
	}
}
