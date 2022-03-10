package com.example.obt.Mysql;

import java.sql.ResultSet;

public class DBHelper {
    private String tableName;
    public DBHelper(String tableName) {
        this.tableName = tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
