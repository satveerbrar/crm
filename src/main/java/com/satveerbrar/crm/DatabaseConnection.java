package com.satveerbrar.crm;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String DATABASE_NAME = System.getenv("DB_NAME");
    private static final String DATABASE_USER = System.getenv("DB_USER");
    private static final String DATABASE_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/" + DATABASE_NAME;


    public Connection getConnection(){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return null;
    }
}
