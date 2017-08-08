package com.yz.code.schema;

import org.springframework.util.StringUtils;

import com.yz.code.util.StringUtil;

public class ColumnSchema {

	private String columnName;
	private int dataType;
	private String dataTypeName;
	private int columnSize;
	private int decimalDigits;
	private boolean nullable;
	private String remarks;
	private String defaultValue;
	private boolean isPrimary;
	private boolean isAutoIncrement;
	private String propertyName;
	private String displayName;
	private String jdbcType;
	private String javaType;
	private String fullJavaType;

	private String propertyVarName;
	private String cPropertyName;
	private boolean lastColumn;


	public String getJavaType() {
		return javaType;
	}

	public boolean isLikeWhere() {
		if (isUpdateTimeColumn()
				|| isCreateTimeColumn()
				|| this.isDateColumn()
				|| DataTypeEnum.BIT.equals(this.dataTypeEnum)) {
			return false;
		}
		return (DataTypeEnum.VARCHAR.equals(dataTypeEnum) && this.columnSize < 100);
	}

	public boolean isInWhere() {
		if (isUpdateTimeColumn() || isCreateTimeColumn() || isDateColumn()) {
			return false;
		}
		return DataTypeEnum.TINYINT.equals(dataTypeEnum)
				|| DataTypeEnum.BIGINT.equals(dataTypeEnum)
				|| DataTypeEnum.INTEGER.equals(dataTypeEnum)
				|| DataTypeEnum.BIT.equals(this.dataTypeEnum)
				|| (DataTypeEnum.VARCHAR.equals(dataTypeEnum) && this.columnSize < 51);
	}

	public boolean isRangeWhere() {
		if (isDateColumn() || DataTypeEnum.DECIMAL.equals(dataTypeEnum)) {
			return true;
		}
		return false;
	}

	public boolean isEqualWhere() {
		if (isDateColumn()
				|| DataTypeEnum.DECIMAL.equals(dataTypeEnum)
				|| (DataTypeEnum.VARCHAR.equals(dataTypeEnum) && columnSize > 100)) {

			return false;
		}
		return true;
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

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	private DataTypeEnum dataTypeEnum;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
		String[] ss = columnName.split("\\_");
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.uncapitalize(ss[0]));
		for (int i = 1; i < ss.length; i++) {
			String s = ss[i];
			sb.append(StringUtils.capitalize(s));
		}
		this.setPropertyName(sb.toString());
		this.setPropertyVarName(StringUtils.uncapitalize(this.getPropertyName()));
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
		dataTypeEnum = DataTypeEnum.get(dataType);
		this.setJdbcType(this.dataTypeEnum.getJdbcType());
		this.setJavaType(this.dataTypeEnum.getJavaType());
		this.setFullJavaType(this.dataTypeEnum.getFullJavaType());
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public Double getColumnLength() {
		Double double1 = new Double(this.columnSize);
		if (decimalDigits > 0) {
			double1 = new Double(columnSize + "." + decimalDigits);
		}
		return double1;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public DataTypeEnum getDataTypeEnum() {
		return dataTypeEnum;
	}

	public void setDataTypeEnum(DataTypeEnum dataTypeEnum) {
		this.dataTypeEnum = dataTypeEnum;
	}

	private String getter;

	public String getGetter()
	{
		if (getter == null) {
			if (dataTypeEnum.getDataType() == DataTypeEnum.BIT.getDataType()) {
				if (this.propertyName.startsWith("is")) {
					getter = this.propertyName;
				}
			}
			getter = "get" + StringUtils.capitalize(this.propertyName);
		}
		return getter;
	}

	public void setGetter(String getterName) {
		this.getter = getterName;
	}

	private String setter;

	public String getSetter()
	{
		if (setter == null) {
			setter = "set" + StringUtils.capitalize(this.propertyName);
		}
		return setter;
	}

	public void setSetter(String setter) {
		this.setter = setter;
	}

	public String getPropertyVarName() {
		return propertyVarName;
	}

	public void setPropertyVarName(String propertyVarName) {
		this.propertyVarName = propertyVarName;
	}

	public String getcPropertyName() {
		if (cPropertyName == null) {
			cPropertyName = StringUtils.capitalize(this.propertyName);
		}
		return cPropertyName;
	}

	public void setcPropertyName(String cPropertyName) {
		this.cPropertyName = cPropertyName;
	}

	public String getTestValue()
	{
		String testValue = "";

		if (DataTypeEnum.VARCHAR.equals(dataTypeEnum)
				|| DataTypeEnum.LONGVARCHAR.equals(dataTypeEnum)) {
			testValue = "\"test string\"";
		}
		else if (DataTypeEnum.BIGINT.equals(dataTypeEnum)) {
			testValue = this.propertyName.endsWith("Time") ? "System.currentTimeMillis()" : "1L";
		}
		else if (DataTypeEnum.INTEGER.equals(dataTypeEnum)) {
			testValue = "1";
		}
		else if (DataTypeEnum.TINYINT.equals(dataTypeEnum)) {
			testValue = "(byte)1";
		}
		else if (DataTypeEnum.DECIMAL.equals(dataTypeEnum)) {
			testValue = "new BigDecimal(10.01)";
		}
		else if (DataTypeEnum.BIT.equals(dataTypeEnum)) {
			testValue = "true";
		}
		else {
			testValue = "null";
		}
		return testValue;
	}

	public String getDisplayName() {
		if (displayName == null) {
			displayName = StringUtil.isNotBlank(this.remarks) ? this.remarks : this.propertyName;
		}
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isEditable()
	{
		if (propertyName.equalsIgnoreCase("id")
				|| propertyName.equalsIgnoreCase("createTime")
				|| propertyName.equalsIgnoreCase("updateTime")
				|| propertyName.equalsIgnoreCase("createUserId")
				|| propertyName.equalsIgnoreCase("updateUserId")
				|| propertyName.equalsIgnoreCase("status")
				|| propertyName.equalsIgnoreCase("isDeleted")) {

			return false;

		}

		return true;
	}

	public boolean isNotNull() {
		return !nullable;
	}

	public boolean isInteger() {
		return this.dataTypeEnum == DataTypeEnum.BIGINT
				|| this.dataTypeEnum == DataTypeEnum.INTEGER
				|| this.dataTypeEnum == DataTypeEnum.TINYINT
				|| this.dataTypeEnum == DataTypeEnum.SMALLINT;
	}

	public boolean isNumber() {
		return this.dataTypeEnum == DataTypeEnum.FLOAT
				|| this.dataTypeEnum == DataTypeEnum.DECIMAL
				|| this.dataTypeEnum == DataTypeEnum.DOUBLE;
	}

	public boolean isString() {
		return this.dataTypeEnum == DataTypeEnum.VARCHAR
				|| this.dataTypeEnum == DataTypeEnum.LONGVARCHAR;
	}

	public boolean isNeedValidate() {
		return isInteger() || isNotNull() || isNumber();
	}

	public String getThDataOptions() {

		StringBuilder sbBuilder = new StringBuilder("field:'" + this.propertyName + "'");
		if (columnName.endsWith("_time")) {
			sbBuilder.append(",formatter:dateFormatter");
		}
		else if (dataTypeEnum.equals(DataTypeEnum.BIT)) {
			sbBuilder.append(",formatter:yesnoFormatter");
		}
		return sbBuilder.toString();
	}

	public String getEasyUIClassForSearch() {
		if (isInteger()) {
			if (columnName.endsWith("_time")) {
				return "easyui-datetimebox";
			}
		}
		return "easyui-textbox";
	}

	public String getEasyUIClassForEdit() {

		if (!isEditable()) {
			return "easyui-textbox";
		}

		if (isString()) {
			if (isNotNull()
					|| columnName.endsWith("email")
					|| columnName.endsWith("url")) {
				return "easyui-textbox";
			}
		}
		else if (isInteger()) {
			if (columnName.endsWith("_time")) {
				return "easyui-datetimebox";
			}
			else {
				return "easyui-numberbox";
			}
		}
		return "easyui-textbox";
	}

	public String getEasyUIInputOptionForEdit() {
		if (!isEditable()) {
			return "";
		}

		StringBuilder sbBuilder = new StringBuilder();
		if (this.isString()) {
			if (isNotNull()) {
				sbBuilder.append("required:true,");
			}
			if (this.propertyName.endsWith("url")) {
				sbBuilder.append("validType:['url','length[0," + this.columnSize + "]'],");
			}
			else if (this.propertyName.endsWith("email")) {
				sbBuilder.append("validType:['email','length[0," + this.columnSize + "]'],");
			}
			else {
				sbBuilder.append("validType:['length[0," + this.columnSize + "]'],");
			}
		}
		if (sbBuilder.length() > 0) {
			String options = sbBuilder.substring(0, sbBuilder.length() - 1);
			return "data-options=\"" + options + "\" ";
		}
		return "";
	}

	public boolean isUpdateUseridColumn() {
		for (String name : TableSchema.UPDATE_USERID_COLUMN) {
			if (name.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;

	}

	public boolean isUpdateTimeColumn() {
		for (String name : TableSchema.UPDATE_TIME_COLUMN) {
			if (name.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDateColumn() {
		return columnName.toUpperCase().endsWith("TIME") || columnName.toUpperCase().endsWith("DATE");
	}

	public boolean isCreateUseridColumn() {
		for (String name : TableSchema.CREATE_USERID_COLUMN) {
			if (name.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCreateTimeColumn() {
		for (String name : TableSchema.CREATE_TIME_COLUMN) {
			if (name.equalsIgnoreCase(columnName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDeletedColumn() {
		return this.columnName.equalsIgnoreCase(TableSchema.DELETED_COLUMN);
	}

	public boolean isStatusColumn() {
		return this.columnName.equalsIgnoreCase(TableSchema.STATUS_COLUMN);
	}
	
	public boolean isAuditStatusColumn() {
		return this.columnName.equalsIgnoreCase(TableSchema.AUDIT_STATUS_COLUMN);
	}

	public boolean isLastColumn() {
		return lastColumn;
	}

	public void setLastColumn(boolean lastColumn) {
		this.lastColumn = lastColumn;
	}
}
