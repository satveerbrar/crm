package com.satveerbrar.crm;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ViewApplicationsController implements Initializable {

  private final ObservableList<ApplicationClient> applications =
      FXCollections.observableArrayList();
  @FXML private TableView<ApplicationClient> applicationsTable;
  @FXML
  private TableColumn<ApplicationClient, String> colFirstName,
      colLastName,
      colApplicationType,
      colApplicationStatus,
      colSubmissionDate,
      colPriority,
      colNotes;
  @FXML private TableColumn<ApplicationClient, Void> colEdit, colDelete;
  @FXML private TextField searchField;

  /**
   * Initializes the controller, setting up the table columns, search functionality, data loading,
   * and edit/delete button actions for the applications table.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    colFirstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
    colLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
    colApplicationType.setCellValueFactory(new PropertyValueFactory<>("ApplicationType"));
    colApplicationStatus.setCellValueFactory(new PropertyValueFactory<>("ApplicationStatus"));
    colSubmissionDate.setCellValueFactory(new PropertyValueFactory<>("SubmissionDate"));
    colPriority.setCellValueFactory(new PropertyValueFactory<>("Priority"));
    colNotes.setCellValueFactory(new PropertyValueFactory<>("Notes"));

    setupSearch();
    loadApplicationData();
    setupEditButton();
    setupDeleteButton();

    Launcher.getLogger().info("ViewApplicationsController initialized.");
  }

  /**
   * Configures an 'Edit' button for each row in the applications table to allow modifications to
   * the selected application.
   */
  private void setupEditButton() {
    colEdit.setCellFactory(
        param ->
            new TableCell<ApplicationClient, Void>() {
              private final Button btn = new Button("Edit");

              {
                btn.setOnAction(
                    event -> {
                      ApplicationClient applicationClient =
                          getTableView().getItems().get(getIndex());
                      editApplication(applicationClient);
                    });
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
              }
            });
  }

  /**
   * Configures the delete button for each row in the applications table. This button enables the
   * deletion of the selected application, with a confirmation prompt before deletion.
   */
  private void setupDeleteButton() {
    colDelete.setCellFactory(
        param ->
            new TableCell<ApplicationClient, Void>() {
              private final Button btn = new Button("Delete");

              {
                btn.setOnAction(
                    event -> {
                      ApplicationClient applicationClient =
                          getTableView().getItems().get(getIndex());
                      deleteApplication(applicationClient);
                    });
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
              }
            });
  }

  /**
   * Loads the application edit form with the selected application's data for editing.
   *
   * @param applicationClient The application client data to be edited.
   */
  private void editApplication(ApplicationClient applicationClient) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("addApplication.fxml"));
      Parent root = loader.load();
      AddEditApplicationController controller = loader.getController();
      controller.setEditingApplicationClient(applicationClient);
      Stage stage = new Stage();
      stage.setScene(new Scene(root));
      stage.setTitle("Edit Application");
      stage.show();
    } catch (IOException e) {
      Launcher.getLogger().error("Error while editing application: {}", e.getMessage());
    }
  }

  /**
   * Prompts for confirmation and call executeDelete method for selected application to delete from
   * database and table view if confirmed.
   *
   * @param applicationClient The application client data to be deleted.
   */
  private void deleteApplication(ApplicationClient applicationClient) {
    if (applicationClient == null || applicationClient.getId() == 0) {
      AlertHelper.showAlert("No application selected to delete!", Alert.AlertType.ERROR);
      Launcher.getLogger().warn("No application selected to delete.");
      return;
    }

    Alert confirmationAlert =
        new Alert(
            Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete this application?",
            ButtonType.YES,
            ButtonType.NO);
    confirmationAlert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.YES) {
                executeDelete(applicationClient);
              }
            });
  }

  /**
   * Executes the deletion of the application in the database and updates the table view if
   * successful.
   *
   * @param applicationClient The application client whose data is to be deleted from the database.
   */
  private void executeDelete(ApplicationClient applicationClient) {
    String sql = "DELETE FROM applications WHERE application_id = ?";

    try (Connection conn = new DatabaseConnection().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, applicationClient.getId());
      int affectedRows = pstmt.executeUpdate();
      if (affectedRows > 0) {
        AlertHelper.showAlert("Application deleted successfully!", Alert.AlertType.INFORMATION);
        applicationsTable.getItems().remove(applicationClient);
        Launcher.getLogger().info("Application deleted successfully.");
      } else {
        AlertHelper.showAlert(
            "No application was deleted. Please try again.", Alert.AlertType.ERROR);
        Launcher.getLogger().warn("No application was deleted.");
      }
    } catch (SQLException e) {
      AlertHelper.showAlert(
          "Error while deleting the application: " + e.getMessage(), Alert.AlertType.ERROR);
      Launcher.getLogger().error("Error while deleting the application: {}", e.getMessage());
    }
  }

  /**
   * Sets up a listener on the search text field to filter the applications table dynamically based
   * on user input.
   */
  private void setupSearch() {
    searchField.textProperty().addListener((obs, oldVal, newVal) -> filterApplications(newVal));
  }

  /**
   * Filters the applications displayed in the table based on the search text input by the user.
   *
   * @param searchText The text used to filter the applications displayed.
   */
  private void filterApplications(String searchText) {
    if (searchText == null || searchText.isEmpty()) {
      applicationsTable.setItems(applications);
    } else {
      ObservableList<ApplicationClient> filteredList = FXCollections.observableArrayList();
      for (ApplicationClient applicationClient : applications) {
        if (applicationClient.matchesSearch(searchText)) {
          filteredList.add(applicationClient);
        }
      }
      applicationsTable.setItems(filteredList);
    }
  }

  /**
   * Loads application data from the database into the table view. Data from 'applications' is
   * joined with 'clients' to provide comprehensive details for each application. Populates the
   * table upon initialization.
   */
  private void loadApplicationData() {
    DatabaseConnection dbConnection = new DatabaseConnection();
    Connection conn = dbConnection.getConnection();

    try (Statement stmt = conn.createStatement();
        ResultSet rs =
            stmt.executeQuery(
                "SELECT a.*, c.FIRST_NAME, c.LAST_NAME FROM applications a JOIN clients c ON a.client_id = c.client_id")) {

      while (rs.next()) {
        applications.add(
            new ApplicationClient(
                rs.getInt("APPLICATION_ID"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getString("APPLICATION_TYPE"),
                rs.getString("APPLICATION_STATUS"),
                rs.getDate("SUBMISSION_DATE").toString(),
                rs.getString("PRIORITY"),
                rs.getString("NOTES")));
      }
      Launcher.getLogger().info("Application data loaded successfully.");
    } catch (Exception e) {
      System.out.println("Error fetching applications: " + e.getMessage());
      Launcher.getLogger().error("Error fetching applications: {}", e.getMessage());
    }
    applicationsTable.setItems(applications);
  }
}
