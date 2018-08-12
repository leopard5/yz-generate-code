/*
 * Copyright 2012-2017 yazhong.qi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yz.code.tools;

import com.yz.code.schema.*;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yazhong.qi
 * @since 1.8.0
 */
public class DataSchema {

    @Autowired
    public BasicDataSource datasource;

    public DatabaseSchema getDatabaseSchema() throws SQLException {
        try (Connection connection = datasource.getConnection();) {
            // ApplicationContextUtil.getBean(BasicDataSource.class);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            DatabaseSchema databaseSchema = new DatabaseSchema();
            databaseSchema.setTables(getTables(databaseSchema));
            return databaseSchema;
        } finally {
        }
    }

    public List<TableSchema> getTables(DatabaseSchema databaseSchema) throws SQLException {
        String[] types = {"table", "view"};
        List<TableSchema> tableSchemas = new ArrayList<TableSchema>();
        try (Connection connection = datasource.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try (ResultSet rs = databaseMetaData.getTables("", "", "", types)) {
                while (rs.next()) {
                    TableSchema tableSchema = new TableSchema();
                    tableSchema.setTableCatalog(rs.getString("TABLE_CAT"));
                    tableSchema.setTableName(rs.getString("TABLE_NAME"));
                    tableSchema.setComment(rs.getString("REMARKS"));
                    if (tableSchema.getComment() == null || tableSchema.getComment().isEmpty()) {
                        tableSchema.setComment(getCommentByTableName(tableSchema.getTableName()));
                    }
                    List<ColumnSchema> columnSchemas = getColumns(tableSchema);
                    tableSchema.setColumns(columnSchemas);
                    tableSchema.setIndexes(getIndexs(tableSchema));
                    tableSchema.setPrimaryKey(getPrimaryKey(tableSchema));
                    tableSchema.setView(rs.getString(4).equals("VIEW"));
                    tableSchemas.add(tableSchema);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return tableSchemas;
        }
    }

    public List<ColumnSchema> getColumns(TableSchema tableSchema) throws SQLException {
        List<ColumnSchema> columnSchemas = new ArrayList<ColumnSchema>();
        try (Connection connection = datasource.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try (ResultSet rs = databaseMetaData.getColumns(tableSchema.getTableCatalog(), null,
                    tableSchema.getTableName(), "%")) {
                while (rs.next()) {
                    // 表目录（可能为空）
                    String tableCat = rs.getString("TABLE_CAT");
                    // 表的架构（可能为空）
                    String tableSchemaName = rs.getString("TABLE_SCHEM");
                    // 表名
                    String tableName_ = rs.getString("TABLE_NAME");
                    // 列名
                    String columnName = rs.getString("COLUMN_NAME");
                    // 对应的java.sql.Types类型
                    int dataType = rs.getInt("DATA_TYPE");
                    // java.sql.Types类型
                    String dataTypeName = rs.getString("TYPE_NAME");
                    // 列大小
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    // 小数位数
                    int decimalDigits = rs.getInt("DECIMAL_DIGITS");
                    // 是否允许为null
                    int nullAble = rs.getInt("NULLABLE");
                    String isNullAble = rs.getString("IS_NULLABLE");
                    // 列描述
                    String remarks = rs.getString("REMARKS");
                    // 默认值
                    String defaultValue = rs.getString("COLUMN_DEF");
                    // sql数据类型
                    int sqlDataType = rs.getInt("SQL_DATA_TYPE");

                    /**
                     * ISO规则用来确定某一列的为空性。 是---如果该参数可以包括空值; 无---如果参数不能包含空值
                     * 空字符串---如果参数为空性是未知的
                     */

                    /**
                     * 指示此列是否是自动递增 是---如果该列是自动递增 无---如果不是自动递增列 空字串---如果不能确定它是否
                     * 列是自动递增的参数是未知
                     */
                    String isAutoincrement = rs.getString("IS_AUTOINCREMENT");
                    // String isGeneratedColumn =
                    // rs.getString("IS_GENERATEDCOLUMN");

                    ColumnSchema columnSchema = new ColumnSchema();
                    columnSchema.setColumnName(columnName);
                    columnSchema.setRemarks(remarks);
                    columnSchema.setColumnSize(columnSize);
                    columnSchema.setDataType(dataType);
                    columnSchema.setDataTypeName(dataTypeName);
                    columnSchema.setAutoIncrement(isAutoincrement.equalsIgnoreCase("YES"));
                    columnSchema.setDecimalDigits(decimalDigits);
                    columnSchema.setDefaultValue(defaultValue);
                    columnSchema.setNullable(nullAble == 1);
                    columnSchemas.add(columnSchema);
                }
                ColumnSchema columnSchema = columnSchemas.get(columnSchemas.size() - 1);
                columnSchema.setLastColumn(true);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return columnSchemas;
        }
    }

    public String getCommentByTableName(String tableName) throws SQLException {
        String comment = null;
        try (Connection connection = datasource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + tableName)) {
            if (rs != null && rs.next()) {
                String createDDL = rs.getString(2);
                comment = parse(createDDL);
                // 将表注释中多余字符去掉
                comment = comment.replaceAll("表", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comment;
    }

    /**
     * 返回注释信息
     *
     * @param all
     * @return
     */

    public static String parse(String all) {
        String comment = null;
        int index = all.indexOf("COMMENT='");
        if (index < 0) {
            return "";
        }
        comment = all.substring(index + 9);
        comment = comment.substring(0, comment.length() - 1);
        return comment;
    }

    public KeySchema getPrimaryKey(TableSchema tableSchema) throws SQLException {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = datasource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            rs = databaseMetaData.getPrimaryKeys(tableSchema.getTableCatalog(), null, tableSchema.getTableName());
            KeySchema keySchema = new KeySchema();
            List<ColumnSchema> memberColumns = new ArrayList<ColumnSchema>();
            keySchema.setMemberColumns(memberColumns);
            String columnName = null;
            String keyName = null;
            while (rs.next()) {
                // 列名
                columnName = rs.getString("COLUMN_NAME");
                for (ColumnSchema column : tableSchema.getColumns()) {
                    if (columnName.equalsIgnoreCase(column.getColumnName())) {
                        column.setPrimary(true);
                        tableSchema.setPrimaryColumn(column);
                        memberColumns.add(column);
                        break;
                    }
                }
                // 对应的java.sql.Types类型
                keyName = rs.getString("PK_NAME");
                keySchema.setKeyName(keyName);
            }
            return keySchema;
        } finally {
            closeConnection(connection);
            if (rs != null) {
                rs.close();
            }
        }
    }

    public List<IndexSchema> getIndexs(TableSchema tableSchema) throws SQLException {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = datasource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            rs = databaseMetaData.getIndexInfo(tableSchema.getTableCatalog(), null, tableSchema.getTableName(), false, true);

            List<IndexSchema> indexSchemas = new ArrayList<IndexSchema>();
            Map<String, IndexSchema> indexColumns = new HashMap<String, IndexSchema>();
            // List<ColumnSchema> memberColumns = new ArrayList<ColumnSchema>();
            // keySchema.setMemberColumns(memberColumns);
            addIndex(tableSchema, rs, indexColumns, indexSchemas);
            // rs = databaseMetaData.getIndexInfo(tableSchema.getTableCatalog(),
            // null, tableSchema.getTableName(), false, true);
            // addIndex(tableSchema, rs, indexColumns, indexSchemas);
            return indexSchemas;
        } finally {
            closeConnection(connection);
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void addIndex(TableSchema tableSchema, ResultSet rs, Map<String, IndexSchema> indexColumns, List<IndexSchema> indexSchemas) throws SQLException {
        while (rs.next()) {
            // 索引的名称
            String indexName = rs.getString("INDEX_NAME");
            if (indexName.equalsIgnoreCase("PRIMARY")) {
                continue;
            }
            // 索引类型
            short type = rs.getShort("TYPE");
            // 索引的名称
            boolean isUnique = !rs.getBoolean("NON_UNIQUE");
            // 列名
            String columnName = rs.getString("COLUMN_NAME");
            // 列排序顺序:升序还是降序
            String ascOrDesc = rs.getString("ASC_OR_DESC");
            IndexSchema indexSchema = null;
            if (!indexColumns.containsKey(indexName)) {
                indexSchema = new IndexSchema();
                indexSchema.setIndexName(indexName);
                indexSchema.setUnique(isUnique);
                indexSchema.setMemberColumns(new ArrayList<ColumnSchema>());
                indexSchemas.add(indexSchema);
                indexColumns.put(indexName, indexSchema);
            } else {
                indexSchema = indexColumns.get(indexName);
            }
            for (ColumnSchema column : tableSchema.getColumns()) {
                if (columnName.equalsIgnoreCase(column.getColumnName())) {
                    indexSchema.getMemberColumns().add(column);
                    break;
                }
            }
        }
    }

    public BasicDataSource getDatasource() {
        return datasource;
    }

    public void setDatasource(BasicDataSource datasource) {
        this.datasource = datasource;
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
