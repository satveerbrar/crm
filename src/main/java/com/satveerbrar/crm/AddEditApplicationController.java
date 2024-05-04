package com.satveerbrar.crm;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller class for managing both addition and editing of client application records. This class
 * handles the interaction logic for the Add/Edit Application form UI.
 */
public class AddEditApplicationController implements Initializable {

  @FXML private TextField clientId, nameField, emailField;
  @FXML
  private ChoiceBox<String> applicationTypeChoiceBox, applicationStatusChoiceBox, priorityChoiceBox;
  @FXML private TextArea notesInput;
  @FXML private Label headerLabel;
  @FXML private Button applicationSubmitButton;
  private ApplicationClient editingApplicationClient;

  /**
   * Initializes the controller class. This method is automatically called after the FXML fields
   * have been populated. Sets up the UI components with initial data and configurations.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeChoiceBoxes();
    setupClientIdChangeListener();
    ButtonUtils.setHoverCursor(applicationSubmitButton);
    Launcher.getLogger().info("AddEditApplicationController initialized.");
  }

  /**
   * Sets up a listener on the clientId text field to dynamically update form fields as the user
   * type client ID in Edit form.
   */
  private void setupClientIdChangeListener() {
    clientId
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue.trim().isEmpty()) {
                updatePromptTexts(newValue.trim());
              }
            });
  }

  /**
   * Updates the form Text fields in real time with client information based on the provided client
   * ID. This method is used by clientId listener only
   *
   * @param clientId The ID of the client whose information is to be fetched and displayed. fields
   */
  private void updatePromptTexts(String clientId) {
    DatabaseConnection dbConnection = new DatabaseConnection();
    String sql = "SELECT first_name, last_name, email FROM clients WHERE client_id = ?";
    try (Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, Integer.parseInt(clientId));
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");

        nameField.setPromptText(firstName.isEmpty() ? "name" : firstName + " " + lastName);
        emailField.setPromptText(email.isEmpty() ? "email" : email);
      } else {
        nameField.setPromptText("name");
        emailField.setPromptText("email");
      }

    } catch (SQLException e) {
      Launcher.getLogger().error("SQL Error: " + e.getMessage(), e);
      AlertHelper.showAlert(
          "Error fetching client details: " + e.getMessage(), Alert.AlertType.ERROR);
    } catch (NumberFormatException e) {
      AlertHelper.showAlert("Invalid Client ID format.", Alert.AlertType.ERROR);
    }
  }

  /**
   * Initializes the choice boxes with predefined data for application type, status and priority
   * levels.
   */
  private void initializeChoiceBoxes() {
    applicationTypeChoiceBox
        .getItems()
        .addAll(
            "Visitor Visa",
            "Study Permit",
            "Work Permit",
            "Permanent Residency",
            "Citizenship",
            "Other");
    applicationTypeChoiceBox.setValue("Select Application Type");

    applicationStatusChoiceBox
        .getItems()
        .addAll("Not Started", "In progress", "Approved", "Rejected");
    applicationStatusChoiceBox.setValue("Select Status");

    priorityChoiceBox.getItems().addAll("Low", "Medium", "High");
    priorityChoiceBox.setValue("Select Priority");
  }

  /**
   * Handles the form submission. It calls for another method validateInputs() before saving to the
   * database.
   *
   * @param actionEvent The event that triggered the submission.
   */
  public void submitForm(ActionEvent actionEvent) {
    if (validateInputs()) {
      saveToDatabase(editingApplicationClient == null);
    } else {
      Launcher.getLogger().warn("Validation failed");
    }
  }

  /**
   * This method gets called from ViewApplicationController. If User clicks Edit button related to
   * any application then it call this method to initialize the form with clients data and change
   * heading of the form to Edit existing application.
   *
   * @param applicationClient This is the applicaiton client which need to edited or null if
   *     creating new application.
   */
  public void setEditingApplicationClient(ApplicationClient applicationClient) {
    this.editingApplicationClient = applicationClient;
    if (applicationClient != null) {
      initializeFormWithClientData();
      headerLabel.setText("Edit Existing Application");
    } else {
      headerLabel.setText("Add New Application");
    }
  }

  /** Initialize the form with existing ApplicationClient data using getters */
  private void initializeFormWithClientData() {
    clientId.setDisable(true);
    applicationTypeChoiceBox.setValue(editingApplicationClient.getApplicationType());
    applicationStatusChoiceBox.setValue(editingApplicationClient.getApplicationStatus());
    priorityChoiceBox.setValue(editingApplicationClient.getPriority());
    notesInput.setText(editingApplicationClient.getNotes());
  }

  /**
   * Validates the client ID from the input field, ensuring it is not only a valid integer but also
   * corresponds to an existing client in the database. This method is pivotal for ensuring that
   * operations are performed on valid client data.
   *
   * @param clientId The client ID as a string from the clientId text field. It should not be null
   *     or empty.
   * @return true if the client ID is valid and corresponds to an existing client, false otherwise.
   * @throws NumberFormatException If the client ID is not a valid integer. This exception is caught
   *     and handled within the method, triggering a user alert.
   * @see DatabaseConnection#getConnection() Uses the DatabaseConnection class to establish a
   *     database connection.
   */
  public boolean validateClientId(String clientId) {

    if (editingApplicationClient != null) {
      return true;
    }

    int clientIdInt;

    if (clientId.isEmpty()) {
      AlertHelper.showAlert("Please enter client_id", Alert.AlertType.ERROR);
      return false;
    }

    try {
      clientIdInt = Integer.parseInt(clientId);
    } catch (NumberFormatException e) {
      Launcher.getLogger().error("Error while converting client_id to int", e);
      AlertHelper.showAlert("Please enter valid client_id", Alert.AlertType.ERROR);
      return false;
    }

    DatabaseConnection dbConnection = new DatabaseConnection();
    String sql = "Select count(*) from clients WHERE client_id = ?";
    try (Connection conn = dbConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, clientIdInt);

      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        int count = rs.getInt(1);
        if (count > 0) {
          return true;
        } else {
          AlertHelper.showAlert("No client found with the provided ID.", Alert.AlertType.ERROR);
          return false;
        }
      }

    } catch (SQLException e) {
      Launcher.getLogger().error("SQL Error: " + e.getMessage(), e);
      AlertHelper.showAlert(
          "Error while fetching client_id: " + e.getMessage(), Alert.AlertType.ERROR);
      return false;
    }
    return true;
  }

  /**
   * @return true if all validations are successful. This method does not perform any logic but
   *     serves the entry point for all validation of every form fields before submission
   */
  private boolean validateInputs() {
    return validateClientId(clientId.getText().trim())
        && validateChoiceBox(
            applicationTypeChoiceBox, "Select Application Type", "application type")
        && validateChoiceBox(applicationStatusChoiceBox, "Select Status", "application status")
        && validateChoiceBox(priorityChoiceBox, "Select Priority", "priority");
  }

  /**
   * Validate that user has selected a valid option in a ChoiceBox before submitting form. This
   * check ensures that the user does not proceed with the default or an improper selection.
   *
   * @param choiceBox The ChoiceBox to validate
   * @param defaultValue default value set when form loads, used to prompt user action. e.g. 'Choose
   *     an application status'
   * @param fieldName It corresponds to the ChoiceBox and help customize the alert message if
   *     validation fails
   * @return true if valid option selected or not equal to default value; false otherwise.
   */
  private boolean validateChoiceBox(
      ChoiceBox<String> choiceBox, String defaultValue, String fieldName) {
    if (choiceBox.getValue() == null || choiceBox.getValue().equals(defaultValue)) {
      AlertHelper.showAlert("Please select " + fieldName, Alert.AlertType.ERROR);
      choiceBox.requestFocus();
      return false;
    }
    return true;
  }

  /**
   * Saves application data to the database by either inserting a new record or updating an existing
   * one. This method determines the operation (insert or update) based on the passed boolean
   * parameter. It also handles user feedback via alerts and manages the UI by closing the editing
   * stage upon successful updates or clearing the form when a new application is added.
   *
   * @param isNew If true, a new application record will be inserted. If false, an existing
   *     application will be updated.
   * @throws SQLException If there is a database access error or other errors related to database
   *     queries.
   * @throws NumberFormatException If the client ID cannot be parsed into an integer, indicating a
   *     format error.
   */
  private void saveToDatabase(boolean isNew) {
    String sql =
        isNew
            ? "INSERT INTO applications (application_type, application_status, priority, submission_date, notes, client_id) VALUES (?, ?, ?, ?, ?, ?)"
            : "UPDATE applications SET application_type = ?, application_status = ?, priority = ?, submission_date = ?, notes = ? WHERE application_id = ?";

    try (Connection conn = new DatabaseConnection().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, applicationTypeChoiceBox.getValue());
      pstmt.setString(2, applicationStatusChoiceBox.getValue());
      pstmt.setString(3, priorityChoiceBox.getValue());
      pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
      pstmt.setString(5, notesInput.getText());
      if (!isNew) {
        pstmt.setInt(6, editingApplicationClient.getId());
      } else {
        pstmt.setInt(6, Integer.parseInt(clientId.getText().trim()));
      }

      int affectedRows = pstmt.executeUpdate();
      if (affectedRows > 0) {
        Launcher.getLogger()
            .info("Application " + (isNew ? "added" : "updated") + " successfully!");
        AlertHelper.showAlert(
            "Application " + (isNew ? "added" : "updated") + " successfully!",
            Alert.AlertType.CONFIRMATION);
        if (!isNew) {
          closeStage();
        } else {
          clearForm();
        }
      } else {
        AlertHelper.showAlert("No changes were made.", Alert.AlertType.ERROR);
      }

    } catch (SQLException e) {
      Launcher.getLogger().error("SQL Error: " + e.getMessage(), e);
      AlertHelper.showAlert(
          "Error while saving to database: " + e.getMessage(), Alert.AlertType.ERROR);

    } catch (NumberFormatException e) {
      Launcher.getLogger().error("Invalid Client ID format.", e);
      AlertHelper.showAlert("Invalid Client ID format.", Alert.AlertType.ERROR);
    }
  }

  /**
   * Clears all user input fields and resets the selection in choice boxes to their default states.
   * This method is typically called after successfully adding a new application to reset the form,
   * preparing it for another entry if needed.
   */
  private void clearForm() {
    clientId.clear();
    notesInput.clear();
    applicationTypeChoiceBox.setValue("Select Application Type");
    applicationStatusChoiceBox.setValue("Select Status");
    priorityChoiceBox.setValue("Select Priority");
  }

  /**
   * Closes the current stage (window). This method is typically called after an existing
   * application has been successfully updated, allowing the user to return to the main application
   * interface.
   */
  private void closeStage() {
    Stage stage = (Stage) applicationTypeChoiceBox.getScene().getWindow();
    stage.close();
  }
}
