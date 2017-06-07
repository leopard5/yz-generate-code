package com.yz.code.schema;

import java.util.List;

public class KeySchema {

	private String keyName;
	private List<ColumnSchema> memberColumns;

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public List<ColumnSchema> getMemberColumns() {
		
		return memberColumns;
	}

	public void setMemberColumns(List<ColumnSchema> memberColumns) {
		this.memberColumns = memberColumns;
	}
}
