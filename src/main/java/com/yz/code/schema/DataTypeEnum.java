package com.yz.code.schema;

public enum DataTypeEnum {

	BIT(-7, "BOOLEAN", "Boolean", "java.lang.Boolean"),
	TINYINT(-6, "TINYINT", "Byte", "java.lang.Byte"),
	SMALLINT(5, "SMALLINT", "Integer", "java.lang.Integer"),
	INTEGER(4, "INTEGER", "Integer", "java.lang.Integer"),
	BIGINT(-5, "BIGINT", "Long", "java.lang.Long"),
	FLOAT(6, "FLOAT", "Float", "java.lang.Float"),
	DOUBLE(8, "DOUBLE", "Double", "java.lang.Double"),
	NUMERIC(2, "NUMERIC", "Number", "java.lang.Number"),
	DECIMAL(3, "DECIMAL", "BigDecimal", "java.math.BigDecimal"),
	CHAR(1, "CHAR", "Character", "java.lang.Character"),

	VARCHAR(12, "VARCHAR", "String", "java.lang.String"),
	LONGVARCHAR(-1, "LONGVARCHAR", "String", "java.lang.String"),
	DATE(91, "DATE", "Date", "java.util.Date"),
	TIME(92, "TIME", "Time", "java.sql.Time"),
	// TIMESTAMP(93, "TIMESTAMP", "Timestamp", "java.sql.Timestamp"), 
	TIMESTAMP(93, "TIMESTAMP", "Date", "java.util.Date"), 
	// TEXT(0, "VARCHAR", "String", "java.lang.String"),

	
	;
	private int dataType;
	private String jdbcType;
	private String javaType;
	private String fullJavaType;

	DataTypeEnum(int dataType, String jdbcType, String javaType, String fullJavaType)
	{
		this.setDataType(dataType);
		this.setJdbcType(jdbcType);
		this.setJavaType(javaType);
		this.setFullJavaType(fullJavaType);
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getFullJavaType() {
		return fullJavaType;
	}

	public void setFullJavaType(String fullJavaType) {
		this.fullJavaType = fullJavaType;
	}

	public static DataTypeEnum get(int dataType)
	{
		DataTypeEnum[] values = DataTypeEnum.values();
		for (DataTypeEnum dataTypeEnum : values) {
			if (dataTypeEnum.getDataType() == dataType) {
				return dataTypeEnum;
			}
		}
		return null;
	}

	public boolean equals(DataTypeEnum other) {
		return this.dataType == other.dataType;

	}

}
