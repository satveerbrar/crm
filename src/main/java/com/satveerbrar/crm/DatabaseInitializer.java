package com.satveerbrar.crm;

import java.sql.*;

public class DatabaseInitializer {
  private static final String DATABASE_URL = "jdbc:sqlite:crm.db";

  public static void initializeDatabase() {
    try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
      if (conn != null) {
        createTableUserAccount(conn);
        createTableClients(conn);
        createTableApplications(conn);
      }
    } catch (SQLException e) {
      Launcher.getLogger().error("Error during database initialization: {}", e.getMessage(), e);
    }
  }

  private static void createTableUserAccount(Connection conn) throws SQLException {
    DatabaseMetaData meta = conn.getMetaData();
    ResultSet res = meta.getTables(null, null, "user_accounts", new String[] {"TABLE"});
    if (!res.next()) {
      Launcher.getLogger().info("Creating user_accounts table if not exists...");
      String sql =
          "CREATE TABLE IF NOT EXISTS user_accounts ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
              + "firstname TEXT NOT NULL, "
              + "lastname TEXT NOT NULL, "
              + "username TEXT NOT NULL, "
              + "password TEXT NOT NULL);";

      try (Statement stmt = conn.createStatement()) {
        stmt.execute(sql);
        insertInitialData(conn);
      } catch (SQLException e) {
        Launcher.getLogger().error("Error creating user_accounts table: {}", e.getMessage(), e);
      }
    }
  }

  private static void createTableClients(Connection conn) throws SQLException {
    Launcher.getLogger().info("Creating clients table if not exists...");
    String sqlCreateClientsTable =
        "CREATE TABLE IF NOT EXISTS clients ("
            + "client_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "first_name VARCHAR(45), "
            + "last_name VARCHAR(45), "
            + "email VARCHAR(45), "
            + "phone_number VARCHAR(45), "
            + "reference VARCHAR(45), "
            + "citizenship VARCHAR(45), "
            + "notes VARCHAR(255), "
            + "date DATE);";
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(sqlCreateClientsTable);
    } catch (SQLException e) {
      Launcher.getLogger().error("Error creating clients table: {}", e.getMessage(), e);
    }
  }

  private static void createTableApplications(Connection conn) throws SQLException {
    Launcher.getLogger().info("Creating applications table if not exists...");
    String sqlCreateApplicationsTable =
        "CREATE TABLE IF NOT EXISTS applications ("
            + "application_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "application_type VARCHAR(45), "
            + "application_status VARCHAR(45), "
            + "priority VARCHAR(45), "
            + "submission_date DATE, "
            + "client_id INTEGER, "
            + "notes VARCHAR(255), "
            + "FOREIGN KEY(client_id) REFERENCES clients(client_id));";
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(sqlCreateApplicationsTable);
    } catch (SQLException e) {
      Launcher.getLogger().error("Error creating applications table: {}", e.getMessage(), e);
    }
  }

  private static void insertInitialData(Connection conn) {
    Launcher.getLogger().info("Inserting initial data to user_accounts table");
    String sqlInsert =
        "INSERT INTO user_accounts (firstname, lastname, username, password) VALUES (?, ?, ?, ?)";
    try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
      pstmt.setString(1, "Satveer");
      pstmt.setString(2, "Brar");
      pstmt.setString(3, "admin");
      pstmt.setString(4, "admin");
      pstmt.executeUpdate();
    } catch (SQLException e) {
      Launcher.getLogger()
          .error("Error inserting initial data into user_accounts table: {}", e.getMessage(), e);
    }
  }
}
