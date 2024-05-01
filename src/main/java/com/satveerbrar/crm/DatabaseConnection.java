package com.satveerbrar.crm;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String DATABASE_PATH = "crm.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;


    public Connection getConnection(){

        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DATABASE_URL);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
}
