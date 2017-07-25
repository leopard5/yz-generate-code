package com.yz.code.db;

import com.yz.code.entity.DbTable;
import com.yz.code.entity.DbTableColumn;
import com.yz.code.entity.MysqlDbColumn;
import com.yz.code.form.MySqlSetPanel;
import com.yz.code.util.CommonUtils;
import com.yz.code.util.IniReader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class DBHelper {

    private static DBConnection dbconn = new DBConnection();

    public static String ip = "192.168.10.51";
    public static String port = "3306";
    public static String schema = "community";
    public static String encode = "utf-8";
    public static String userName = "root";
    public static String pwd = "password";
    public static String sections = "";

    public static void initDbConfig() {
        try {
            IniReader reader = IniReader.getIniReader();
            sections = reader.getValue("use_db_type", "db", "type");
            if (sections == null || sections.trim().equals("")) {
                sections = MySqlSetPanel.sections;
            }
            HashMap<String, String> cfg = reader.getActiveCfg(sections);
            if (cfg != null) {
                setConfigValue(cfg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setConfigValue(HashMap<String, String> cfg) {
        ip = cfg.get("host");
        port = cfg.get("port");
        schema = cfg.get("db_name");
        encode = cfg.get("encode");
        userName = cfg.get("userName");
        pwd = cfg.get("password");
        dbconn.setDbInfo(ip, port, schema, encode, userName, pwd);
    }

    public static void main(String[] args) {

        List<MysqlDbColumn> tableList = getTableColumns("community", "cmu_group");
        System.out.println(tableList);
    }

    public static List<DbTable> getDbTables(String schema) {
        List<DbTable> tableList = null;
        String sql = "SELECT * FROM information_schema.tables WHERE table_schema = ?";
        try {
            PreparedStatement stmt = dbconn.getPreparedStatement(sql);
            if (stmt != null) {
                stmt.setString(1, schema);
                ResultSet rs = stmt.executeQuery();
                if (rs != null) {
                    tableList = new ArrayList<DbTable>();
                    DbTable table = null;
                    while (rs.next()) {
                        table = new DbTable();
                        table.setTableSchema(schema).setTableName(rs.getString("table_name"))
                                .setTableComment(rs.getString("table_comment"))
                                .setCreateTime(rs.getTimestamp("create_time"));
                        tableList.add(table);
                    }
                }
                dbconn.close(rs, stmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableList;
    }

    public static List<MysqlDbColumn> getTableColumns(String schema, String table) {
        List<MysqlDbColumn> columnList = new ArrayList<MysqlDbColumn>();
        String sql = "SELECT DISTINCT * FROM information_schema.COLUMNS WHERE table_schema = ? AND table_name = ? ORDER BY ORDINAL_POSITION";
        try {
            PreparedStatement stmt = dbconn.getPreparedStatement(sql);
            if (stmt != null) {
                stmt.setString(1, schema);
                stmt.setString(2, table);
                ResultSet rs = stmt.executeQuery();
                if (rs != null) {
                    MysqlDbColumn column = null;
                    while (rs.next()) {
                        column = new MysqlDbColumn();
                        column.setTableSchema(schema).setTableName(table).setColumnName(rs.getString("column_name"))
                                .setColumnType(rs.getString("column_type"))
                                .setColumnDefault(rs.getObject("column_default"))
                                .setColumnKey(rs.getString("column_key"))
                                .setColumnComment(rs.getString("column_comment")).setDataType(rs.getString("data_type"))
                                .setCharacterMaximumLength(rs.getInt("character_maximum_length"))
                                .setCharacterOctetLength(rs.getInt("character_octet_length"))
                                .setExtra(rs.getString("extra")).setIsNullable(rs.getString("is_nullable"))
                                .setPrivileges(rs.getString("privileges"))
                                .setOrdinalPosition(rs.getInt("ordinal_position"));
                        columnList.add(column);
                    }
                }
                dbconn.close(rs, stmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnList;
    }

    public static Map<String, List<DbTableColumn>> getMutilTableColumns(String schema) {
        Map<String, List<DbTableColumn>> tableMap = new HashMap<String, List<DbTableColumn>>();
        String sql = "SELECT DISTINCT * FROM information_schema.COLUMNS WHERE table_schema = ? AND table_name in ("
                + "SELECT table_name FROM information_schema.tables WHERE table_schema = ?"
                + ") ORDER BY table_name, ORDINAL_POSITION";
        try {
            PreparedStatement stmt = dbconn.getPreparedStatement(sql);
            if (stmt != null) {
                stmt.setString(1, schema);
                stmt.setString(2, schema);
                ResultSet rs = stmt.executeQuery();
                if (rs != null) {
                    MysqlDbColumn column = null;
                    List<DbTableColumn> columnList = null;
                    String tempName = "", tname = "";
                    ;
                    while (rs.next()) {
                        column = new MysqlDbColumn();
                        tname = rs.getString("table_name");
                        if (!tempName.equals(tname)) {
                            if (columnList != null) {
                                tableMap.put(tempName, columnList);
                            }
                            tempName = tname;
                            columnList = new ArrayList<DbTableColumn>();
                        }
                        column.setTableSchema(schema)
                                .setTableName(tname)
                                .setColumnName(rs.getString("column_name"))
                                .setColumnType(rs.getString("column_type"))
                                .setColumnDefault(rs.getObject("column_default"))
                                .setColumnKey(rs.getString("column_key"))
                                .setColumnComment(rs.getString("column_comment"))
                                .setDataType(rs.getString("data_type"))
                                .setCharacterMaximumLength(rs.getInt("character_maximum_length"))
                                .setCharacterOctetLength(rs.getInt("character_octet_length"))
                                .setExtra(rs.getString("extra"))
                                .setIsNullable(rs.getString("is_nullable"))
                                .setPrivileges(rs.getString("privileges"))
                                .setOrdinalPosition(rs.getInt("ordinal_position"));
                        columnList.add(column);
                    }
                    if (columnList != null) {
                        tableMap.put(tempName, columnList);
                    }
                }
                dbconn.close(rs, stmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableMap;
    }

    public static Map<String, Object> loadTableData(String sql) throws SQLException {
        Map<String, Object> map = null;
        Vector<Vector<Object>> dataVector = null;
        PreparedStatement stmt = dbconn.getPreparedStatement(sql);
        if (stmt != null) {
            ResultSet rs = stmt.executeQuery();
            if (rs != null) {
                map = new HashMap<String, Object>();
                dataVector = new Vector<Vector<Object>>();
                Vector<Object> vector = null;
                Vector<Object> column = null;
                Object value = null;
                int size = 0;
                int row = 1;
                while (rs.next()) {
                    vector = new Vector<Object>();
                    vector.add(row);
                    row++;
                    try {
                        if (column == null) {
                            column = resultSetMetaDataToVector(rs.getMetaData());
                            size = column.size();
                        }
                        for (int i = 1; i < size; i++) {
                            value = rs.getObject(CommonUtils.objectToString(column.get(i)));
                            if (value == null) {
                                vector.add("null");
                            } else {
                                vector.add(value);
                            }
                        }
                    } catch (Exception e) {
                    }
                    dataVector.add(vector);
                }
                map.put("dataSet", dataVector);
                map.put("feildSet", column);
            }
            dbconn.close(rs, stmt);
        }
        return map;
    }

    private static Vector<Object> resultSetMetaDataToVector(ResultSetMetaData rsData) {
        Vector<Object> vector = null;
        try {
            int size = rsData.getColumnCount();
            vector = new Vector<Object>();
            vector.add("序号");
            for (int i = 0; i < size; i++) {
                vector.add(rsData.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            vector = new Vector<Object>();
        }
        return vector;
    }

}
