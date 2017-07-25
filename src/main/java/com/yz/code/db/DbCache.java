package com.yz.code.db;

import com.yz.code.entity.DbTable;
import com.yz.code.entity.DbTableColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DbCache {

    public static List<DbTable> mysqlDbTableList = new ArrayList<DbTable>();

    public static Map<String, List<DbTableColumn>> mysqlDbColumnMap = new HashMap<String, List<DbTableColumn>>();

}
