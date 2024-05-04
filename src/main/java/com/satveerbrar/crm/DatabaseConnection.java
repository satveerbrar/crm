package com.satveerbrar.crm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the database connection for the CRM application. This class handles the creation and
 * configuration of connections to a SQLite database.
 */
public class DatabaseConnection {
  private static final String DATABASE_PATH = "crm.db";
  private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;

  /**
   * Establishes and returns a connection to the database. It also ensures that foreign key support
   * is enabled for the session.
   *
   * @return A Connection object to the database. If the connection attempt fails, returns null.
   */
  public Connection getConnection() {
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
