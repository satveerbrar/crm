package com.satveerbrar.crm;

import java.sql.*;

/**
 * Handles the initialization of the database for the CRM application. This class checks for the
 * existence of necessary tables and creates them if they do not exist. It also populates the
 * user_accounts table with initial data.
 */
public class DatabaseInitializer {
  private static final String DATABASE_URL = "jdbc:sqlite:crm.db";

  /**
   * Initializes the database by establishing a connection and creating tables for user accounts,
   * clients, and applications if they do not already exist. This method ensures the database is
   * ready for use by the application.
   */
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

  /**
   * Checks for the existence of the 'user_accounts' table and creates it if it does not exist.
   *
   * @param conn The connection to the database.
   * @throws SQLException If there is an error in executing the SQL commands.
   */
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

  /**
   * Creates the 'clients' table if it does not exist. This table stores information about the
   * clients including personal details and contact information.
   *
   * @param conn The connection to the database.
   * @throws SQLException If there is an error in executing the SQL commands.
   */
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

  /**
   * Creates the 'applications' table if it does not exist. This table stores application details
   * linked to clients, including application type, status, and related notes.
   *
   * @param conn The connection to the database.
   * @throws SQLException If there is an error in executing the SQL commands.
   */
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

  /**
   * Inserts initial data into the 'user_accounts' table to ensure there are default login
   * credentials available immediately after database setup.
   *
   * @param conn The database connection to use for the insert operation.
   * @throws SQLException If there is an error in executing the insert command.
   */
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
