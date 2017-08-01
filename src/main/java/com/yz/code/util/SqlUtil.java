package com.yz.code.util;

import java.util.List;

import com.yz.code.constant.Constants;
import org.springframework.util.StringUtils;

import com.yz.code.schema.ColumnSchema;
import com.yz.code.schema.IndexSchema;
import com.yz.code.schema.TableSchema;

public class SqlUtil {

	public static String getSelectedFileds(List<ColumnSchema> columns) {
		StringBuilder sb = new StringBuilder();
		for (ColumnSchema col : columns) {
			if (ColumnUtil.isLastColumn(columns, col)) {
				sb.append(col.getColumnName());
			}
			else {
				sb.append(col.getColumnName());
				sb.append(Constants.TABLE_NAME_SEPARATOR);
			}
		}
		return sb.toString();
	}

	public static String getInsertFileds(TableSchema table) {
		List<ColumnSchema> columns = table.getColumns();
		StringBuilder sb = new StringBuilder();
		for (ColumnSchema col : columns) {
			if (col.isAutoIncrement()
					|| col.isPrimary()
					|| col.getColumnName().equalsIgnoreCase("is_deleted")) {
				continue;
			}
			sb.append("\t\t\t" + col.getColumnName() + ",\n");
		}
		return sb.substring(0, sb.length() - 2);
	}

	private final static String PARAM_FORMAT_STRING = "#{%s,jdbcType=%s}";

	public static String getInsertValues(TableSchema table) {
		StringBuilder sb = new StringBuilder();
		for (ColumnSchema col : table.getColumns()) {
			if (col.isAutoIncrement()
					|| col.isPrimary()
					|| col.getColumnName().equalsIgnoreCase("is_deleted")) {
				continue;
			}
			if (col.isUpdateUseridColumn()) {
				sb.append("\t\t\t" + String.format(PARAM_FORMAT_STRING,
						table.getCreateUserIdColumn().getPropertyName(),
						table.getCreateUserIdColumn().getJdbcType()) + ",\n");
			}
			else if (col.isUpdateTimeColumn() || col.isCreateTimeColumn()) {
				sb.append("\t\t\t UNIX_TIMESTAMP(),\n");
			}
			else {
				sb.append("\t\t\t" + String.format(PARAM_FORMAT_STRING,
						col.getPropertyName(),
						col.getJdbcType()) + ",\n");
			}
		}
		return sb.substring(0, sb.length() - 2);
	}

	public final static String getUpdateFileds(TableSchema table) {

		String pattern = "\t\t\t%s = #{%s,jdbcType=%s},\n";

		String pattern01 = "\t\t\t<if test=\"%s!=null and %s!=''\">%s = #{%s,jdbcType=s},</if>\n";

		String pattern02 = "\t\t\t<if test=\"%s!=null\">%s = #{%s,jdbcType=%s},</if>\n";

		StringBuilder sb = new StringBuilder();

		for (ColumnSchema col : table.getColumns()) {
			if (col.isAutoIncrement()
					|| col.isPrimary()
					|| col.isDeletedColumn()
					|| col.isStatusColumn()
					|| col.isCreateTimeColumn()
					|| col.isCreateUseridColumn()
					|| col.isUpdateTimeColumn()
					|| col.isUpdateUseridColumn()) {
				continue;
			}
			if (col.isString()) {
				if (col.isNotNull()) {
					sb.append(String.format(pattern01,
							col.getPropertyName(),
							col.getPropertyName(),
							col.getColumnName(),
							col.getPropertyName(),
							col.getJdbcType()));
				}
				else {
					sb.append(String.format(pattern02,
							col.getPropertyName(),
							col.getColumnName(),
							col.getPropertyName(),
							col.getJdbcType()));
				}
			}
		}

		for (ColumnSchema col : table.getColumns()) {
			if (col.isAutoIncrement()
					|| col.isPrimary()
					|| col.isDeletedColumn()
					|| col.isStatusColumn()
					|| col.isCreateTimeColumn()
					|| col.isCreateUseridColumn()
					|| col.isUpdateTimeColumn()
					|| col.isUpdateUseridColumn()) {
				continue;
			}
			if (!col.isString()) {
				sb.append(String.format(pattern02,
						col.getPropertyName(),
						col.getColumnName(),
						col.getPropertyName(),
						col.getJdbcType()));
			}
		}
		if (table.hasUpdateColumn()) {
			sb.append(String.format(pattern,
					table.getUpdateUserIdColumn().getColumnName(),
					table.getUpdateUserIdColumn().getPropertyName(),
					table.getUpdateUserIdColumn().getJdbcType()));

			sb.append("\t\t\tupdate_time = UNIX_TIMESTAMP(),\n");
		}
		return sb.substring(0, sb.length() - 2);
	}

	public static String getPrimaryWhere(List<ColumnSchema> columns) {
		String pattern = "WHERE %s = #{%s,jdbcType=%s} ";
		for (ColumnSchema col : columns) {
			if (col.isPrimary()) {
				return String.format(pattern,
						col.getColumnName(),
						col.getPropertyName(), col.getJdbcType());
			}
		}
		return "";
	}

	public static String getIndexSelectName(IndexSchema index) {
		StringBuilder sb = new StringBuilder("selectBy");
		for (ColumnSchema col : index.getMemberColumns()) {
			sb.append(StringUtils.capitalize(col.getPropertyName()));
		}
		return sb.toString();
	}

	public static String getIndexBizName(IndexSchema index) {
		StringBuilder sb = new StringBuilder("getBy");
		if (!index.isUnique()) {
			sb = new StringBuilder("getListBy");
		}
		for (ColumnSchema col : index.getMemberColumns()) {
			sb.append(StringUtils.capitalize(col.getPropertyName()));
		}
		return sb.toString();
	}

	public static String getIndexWhere(IndexSchema index) {
		StringBuilder sb = new StringBuilder("WHERE ");
		String pattern = " %s = #{%s,jdbcType=%s}";
		String pattern02 = "\n\t\tAND %s = #{%s,jdbcType=%s} ";
		for (ColumnSchema col : index.getMemberColumns()) {
			if (ColumnUtil.isFirstColumn(index.getMemberColumns(), col)) {
				sb.append(
						String.format(pattern,
								col.getColumnName(),
								col.getPropertyName(),
								col.getJdbcType())
						);
			}
			else {
				sb.append(
						String.format(pattern02,
								col.getColumnName(),
								col.getPropertyName(),
								col.getJdbcType())
						);
			}

		}
		return sb.toString();
	}

	public static  String getDictSQL(){
		StringBuilder sb = new StringBuilder(50);
		sb.append("SELECT ");
		sb.append("dict_item,dict_id,dict_value,dict_name");
		sb.append(" FROM ");
		sb.append(" mmc_dictionary ");
		sb.append(" WHERE ");
		sb.append(" STATUS = 1 ");
		return sb.toString();
	}
}
