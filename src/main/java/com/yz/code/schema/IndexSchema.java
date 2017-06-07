package com.yz.code.schema;

import java.util.List;

public class IndexSchema {

	private String indexName;
	private boolean isUnique;
	private List<ColumnSchema> memberColumns;

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public List<ColumnSchema> getMemberColumns() {
		return memberColumns;
	}

	public void setMemberColumns(List<ColumnSchema> memberColumns) {
		this.memberColumns = memberColumns;
	}

}
