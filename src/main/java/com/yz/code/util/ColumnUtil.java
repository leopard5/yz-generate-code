package com.yz.code.util;

import java.util.List;

import com.yz.code.schema.ColumnSchema;

public class ColumnUtil {
	
	public static boolean isLastColumn(List<ColumnSchema> columns, ColumnSchema col) {
		return columns.indexOf(col) == (columns.size() - 1);
	}

	public static boolean isFirstColumn(List<ColumnSchema> columns, ColumnSchema col) {
		return columns.indexOf(col) == 0;
	}
}
