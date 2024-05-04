package com.satveerbrar.crm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class HomeController implements Initializable {

  @FXML
  private Label homeNavButton,
      addClientNavButton,
      addApplicationsNavButton,
      viewApplicationNavButton,
      viewClientsNavButton,
      totalClientsLabel,
      totalApplicationsLabel,
      totalNotStartedLabel,
      totalInProgressLabel,
      totalApprovedLabel,
      totalRejectedLabel;

  @FXML private BorderPane bp;
  @FXML private AnchorPane ap;

  /**
   * Initializes the controller by setting hover effects on navigation labels and loading initial
   * dashboard statistics. It also sets up the central panel of the BorderPane with the main
   * application view.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    applyHoverCursorToLabels(
        homeNavButton,
        addClientNavButton,
        addApplicationsNavButton,
        viewApplicationNavButton,
        viewClientsNavButton);

    loadDashboards();
    Launcher.getLogger().info("HomeController initialized");
  }

  /**
   * Applies a hand cursor effect to the specified labels on hover. This method utilize the {@link
   * ButtonUtils#setHoverCursor} which accepts any node and modify its cursor on hover.
   *
   * @param labels Varargs parameter that takes one or more labels to which the hover effect will be
   *     applied.
   */
  private void applyHoverCursorToLabels(Label... labels) {
    for (Label label : labels) {
      ButtonUtils.setHoverCursor(label);
    }
  }

  /**
   * Loads and updates the dashboard statistics for total clients, applications, and the status of
   * applications from the database. This method is called during initialization and when the home
   * page is reloaded.
   */
  private void loadDashboards() {
    DatabaseConnection dbConnection = new DatabaseConnection();
    try (Connection conn = dbConnection.getConnection()) {
      updateLabelWithCount(conn, "SELECT COUNT(*) FROM clients", totalClientsLabel);
      updateLabelWithCount(conn, "SELECT COUNT(*) FROM applications", totalApplicationsLabel);
      updateLabelWithCount(
          conn,
          "SELECT COUNT(*) FROM applications WHERE application_status = 'Not Started'",
          totalNotStartedLabel);
      updateLabelWithCount(
          conn,
          "SELECT COUNT(*) FROM applications WHERE application_status = 'In Progress'",
          totalInProgressLabel);
      updateLabelWithCount(
          conn,
          "SELECT COUNT(*) FROM applications WHERE application_status = 'Approved'",
          totalApprovedLabel);
      updateLabelWithCount(
          conn,
          "SELECT COUNT(*) FROM applications WHERE application_status = 'Rejected'",
          totalRejectedLabel);
    } catch (SQLException e) {
      Launcher.getLogger().error("Error executing SQL query: {}", e.getMessage(), e);
    }
  }

  /**
   * Executes a SQL query to count rows based on the specified query and updates the given label
   * with the count. If the query fails, the label will display 'Error'.
   *
   * @param conn The database connection object to use for executing the SQL query.
   * @param query The SQL query string that returns a count.
   * @param label The label to update with the result of the query.
   */
  private void updateLabelWithCount(Connection conn, String query, Label label) {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      if (rs.next()) {
        int count = rs.getInt(1);
        label.setText(String.valueOf(count));
      }
      Launcher.getLogger().info("Label update successfully {}", label);
    } catch (SQLException e) {
      Launcher.getLogger().error("Error executing SQL query: {}", e.getMessage(), e);
      label.setText("Error");
    }
  }

  /**
   * Loads a specified page into the center of the BorderPane. The page is specified by the filename
   * of the FXML file that should be loaded.
   *
   * @param page The name of the page (FXML file) to load, without the '.fxml' extension.
   * @throws RuntimeException if the specified FXML file cannot be loaded.
   */
  public void loadPage(String page) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(page + ".fxml"));
      Parent root = loader.load();
      Launcher.getLogger().info("Loading page: {}", page);
      bp.setCenter(root);
    } catch (IOException e) {
      Launcher.getLogger().error("Failed to load page {}: {}", page, e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Handles the click event on the home navigation label. This method reloads the dashboard
   * statistics and resets the center of the BorderPane to the default view.
   *
   * @param event The mouse event that triggered this action.
   */
  @FXML
  private void homePage(MouseEvent event) {
    Launcher.getLogger().info("Navigating to home page");
    loadDashboards();
    bp.setCenter(ap);
  }

  /**
   * Handles the click event on the navigation label to load the addClient form.
   *
   * @param event The mouse event that triggered this action.
   */
  @FXML
  private void addClient(MouseEvent event) {
    loadPage("addClient");
  }

  /**
   * Handles the click event on the navigation label to load the addApplication form.
   *
   * @param event The mouse event that triggered this action.
   */
  @FXML
  private void addApplication(MouseEvent event) {
    loadPage("addApplication");
  }

  /**
   * Handles the click event on the navigation label to load the viewApplication form.
   *
   * @param event The mouse event that triggered this action.
   */
  @FXML
  private void viewApplications(MouseEvent event) {
    loadPage("viewApplications");
  }

  /**
   * Handles the click event on the navigation label to load the viewClients form.
   *
   * @param event The mouse event that triggered this action.
   */
  @FXML
  private void viewClients(MouseEvent event) {
    loadPage("viewClients");
  }
}
