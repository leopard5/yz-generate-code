package com.yz.code.schema;

import java.util.List;

public class DatabaseSchema {
	private List<TableSchema> tables;

	public List<TableSchema> getTables() {
		return tables;
	}

	public void setTables(List<TableSchema> tables) {
		this.tables = tables;
	}

}
