package com.yz.code.util;

import java.util.List;

import org.springframework.util.StringUtils;

import com.yz.code.schema.ColumnSchema;

public class CodeUtil {

	public static String getParameters(List<ColumnSchema> columns)
	{
		StringBuilder sb = new StringBuilder();
		for (ColumnSchema col : columns) {
			sb.append(col.getJavaType() + " " + col.getPropertyName());
			if (!ColumnUtil.isLastColumn(columns, col)) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static String getParameterValues(List<ColumnSchema> columns)
	{
		StringBuilder sb = new StringBuilder();
		for (ColumnSchema col : columns) {
			sb.append(col.getPropertyName());
			if (!ColumnUtil.isLastColumn(columns, col)) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static String getParameterValues(String modelVar, List<ColumnSchema> columns)
	{
		StringBuilder sb = new StringBuilder();
		for (ColumnSchema col : columns) {
			sb.append(modelVar + "." + col.getGetter() + "()");
			if (!ColumnUtil.isLastColumn(columns, col)) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static String getVarName(String name)
	{
		return StringUtils.uncapitalize(name);
	}

	public static String getDbParameters(List<ColumnSchema> columns)
	{
		String ss = "@Param(\"%s\") %s %s";
		StringBuilder sb = new StringBuilder();
		for (ColumnSchema col : columns) {
			sb.append(String.format(ss, col.getPropertyName(), col.getJavaType(), col.getPropertyName()));
			if (!ColumnUtil.isLastColumn(columns, col)) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
