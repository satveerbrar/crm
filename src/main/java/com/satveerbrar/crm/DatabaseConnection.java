package com.satveerbrar.crm;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DATABASE_PATH = "crm.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;


    public Connection getConnection(){
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            Launcher.getLogger().info("Database connection established successfully");
        } catch (SQLException e) {
            Launcher.getLogger().error("Connection failed: {}", e.getMessage(), e);
        }
        return conn;
    }
}
