package com.yz.code.util;

import java.util.List;

import com.yz.code.schema.ColumnSchema;
import com.yz.code.schema.TableSchema;

public class MapperSql {

	// region Container Properties

	public static String resultMap(TableSchema table) {

		StringBuilder sb = new StringBuilder();
		String idMap = "<id column=\"%s\" property=\"%s\" jdbcType=\"%s\" />\n";
		String resultMap = "<result column=\"%s\" property=\"%s\" jdbcType=\"%s\" />\n";
		List<ColumnSchema> columns = table.getColumns();
		for (ColumnSchema column : columns) {
			if (column.isPrimary()) {
				sb.append(String.format(idMap, column.getPropertyName(), column.getPropertyName(), column.getDataTypeName().toUpperCase()));
			}
			else {
				sb.append(String.format(resultMap, column.getColumnName(), column.getPropertyName(), column.getDataTypeName().toUpperCase()));
			}
		}
		return sb.toString();
	}

	public static String insert(TableSchema table) {

		StringBuilder sbBuilder = new StringBuilder();
		sbBuilder.append("INSERT INTO " + table.getTableName() + "\n");
		sbBuilder.append("(\n");
		sbBuilder.append(SqlUtil.getInsertFileds(table));
		sbBuilder.append(")\n");
		return sbBuilder.toString();
	}

	public static String updateByPrimaryKey(TableSchema table) {

		StringBuilder sbBuilder = new StringBuilder();
		List<ColumnSchema> columns = table.getColumns();
		sbBuilder.append("UPDATE " + table.getTableName() + "\n");
		sbBuilder.append("SET\n");
		sbBuilder.append(SqlUtil.getUpdateFileds(table));
		sbBuilder.append(SqlUtil.getPrimaryWhere(columns));
		return sbBuilder.toString();
	}

	public static String selectByPrimaryKey(TableSchema table) {
		StringBuilder sbBuilder = new StringBuilder();
		sbBuilder.append("SELECT FROM " + table.getTableName() + "\n");
		sbBuilder.append("<include refid=\"selectFields\"/>\n");
		sbBuilder.append(SqlUtil.getPrimaryWhere(table.getColumns()) + "\n");
		return sbBuilder.toString();
	}

	public static String selectAll(TableSchema table) {
		StringBuilder sbBuilder = new StringBuilder();
		sbBuilder.append("SELECT FROM " + table.getTableName() + "\n");
		sbBuilder.append("<include refid=\"selectFields\"/>\n");
		return sbBuilder.toString();
	}

}
