package com.yz.code.util;

import com.yz.code.config.ConfigManager;
import com.yz.code.schema.ColumnSchema;
import org.springframework.util.StringUtils;

import java.util.List;

public class CodeUtil {

    public static String getParameters(List<ColumnSchema> columns) {
        StringBuilder sb = new StringBuilder();
        for (ColumnSchema col : columns) {
            sb.append(col.getJavaType() + " " + col.getPropertyName());
            if (!ColumnUtil.isLastColumn(columns, col)) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String getParameterValues(List<ColumnSchema> columns) {
        StringBuilder sb = new StringBuilder();
        for (ColumnSchema col : columns) {
            sb.append(col.getPropertyName());
            if (!ColumnUtil.isLastColumn(columns, col)) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String getParameterValues(String modelVar, List<ColumnSchema> columns) {
        StringBuilder sb = new StringBuilder();
        for (ColumnSchema col : columns) {
            sb.append(modelVar + "." + col.getGetter() + "()");
            if (!ColumnUtil.isLastColumn(columns, col)) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String getVarName(String name) {
        return StringUtils.uncapitalize(name);
    }

    public static String getDbParameters(List<ColumnSchema> columns) {
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

    public static String getFilePathOfMyBatisGenerator(String fileName) {
        StringBuilder sb = new StringBuilder(20);
        String basePackageString = ConfigManager.getProperty("basePackage");
        String generatorPackage = ConfigManager.getProperty("output.generator.project");
        sb.append(generatorPackage);
        if (!generatorPackage.endsWith("\\")) {
            sb.append("\\");
        }
        String[] split = basePackageString.split("\\.");
        for (String s : split) {
            sb.append(s).append("\\");
        }
        sb.append(fileName).append("\\");
        return sb.toString();
    }
}
