package com.satveerbrar.crm;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddEditClientController implements Initializable {

  private static final Pattern EMAIL_REGEX =
      Pattern.compile(
          "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
  private static final Pattern PHONE_REGEX = Pattern.compile("^\\+?\\d{10,13}$");
  @FXML
  private TextField firstNameInput,
      lastNameInput,
      emailInput,
      phoneNumberInput,
      referenceInput,
      citizenshipInput;
  @FXML private TextArea notesInput;
  @FXML private Label headerLabel;
  @FXML private Button clientSubmitButton;
  private Client editingClient;

  /**
   * Initializes the controller by setting up the form based on whether a client is being edited.
   * Applies a hover effect to the submit button and loads client data if editing an existing
   * client.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    if (editingClient != null) {
      populateFormData();
    }
    ButtonUtils.setHoverCursor(clientSubmitButton);
    Launcher.getLogger().info("AddEditClientController initialized.");
  }

  /**
   * Sets the client to be edited amd populates the form with the client's data and updates the
   * header label to reflect that an existing client is being edited. If no client is provided, the
   * form is set for adding a new client.
   *
   * @param client The client object to be edited or null if adding a new client.
   */
  public void setEditingClient(Client client) {
    this.editingClient = client;
    if (client != null) {
      populateFormData();
      headerLabel.setText("Edit Existing Client");
    } else {
      headerLabel.setText("Add New Client");
    }
  }

  /**
   * Populates the form fields with data from the client being edited. This method is called when an
   * existing client is set to be edited.
   */
  private void populateFormData() {
    firstNameInput.setText(editingClient.getFirstName());
    lastNameInput.setText(editingClient.getLastName());
    emailInput.setText(editingClient.getEmail());
    phoneNumberInput.setText(editingClient.getPhoneNumber());
    referenceInput.setText(editingClient.getReference());
    citizenshipInput.setText(editingClient.getCitizenship());
    notesInput.setText(editingClient.getNotes());
    Launcher.getLogger().info("Form populated with client data.");
  }

  /**
   * Submits the form data. Validates the input fields before proceeding to save the data. If the
   * validation fails, the submission is aborted, and error messages are displayed.
   */
  public void submitForm() {
    if (validateInputs()) {
      saveClientData();
    }
  }

  /**
   * Validates the input fields for the client form. Checks include non-empty validation for names
   * and format validation for email and phone numbers based on predefined patterns.
   *
   * @return true if all inputs are valid, false otherwise.
   */
  private boolean validateInputs() {
    if (firstNameInput.getText().trim().isEmpty()) {
      AlertHelper.showAlert("First name is required.", Alert.AlertType.ERROR);
      return false;
    }
    if (lastNameInput.getText().trim().isEmpty()) {
      AlertHelper.showAlert("Last name is required.", Alert.AlertType.ERROR);
      return false;
    }
    if (!EMAIL_REGEX.matcher(emailInput.getText().trim()).matches()) {
      AlertHelper.showAlert("Invalid email format.", Alert.AlertType.ERROR);
      return false;
    }
    if (!PHONE_REGEX.matcher(phoneNumberInput.getText().trim()).matches()) {
      AlertHelper.showAlert("Invalid phone number format.", Alert.AlertType.ERROR);
      return false;
    }
    return true;
  }

  /**
   * Saves the client data to the database. Determines whether to insert a new record or update an
   * existing one based on whether an editing client is set. Displays an alert with the result of
   * the operation.
   */
  private void saveClientData() {
    String sql =
        editingClient == null
            ? "INSERT INTO clients (first_name, last_name, email, phone_number, reference, citizenship, notes, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            : "UPDATE clients SET first_name = ?, last_name = ?, email = ?, phone_number = ?, reference = ?, citizenship = ?, notes = ?, date = ? WHERE client_id = ?";
    try (Connection conn = new DatabaseConnection().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      fillPreparedStatement(pstmt);
      int affectedRows = pstmt.executeUpdate();
      if (affectedRows > 0) {
        AlertHelper.showAlert(
            "Client " + (editingClient == null ? "added" : "updated") + " successfully!",
            Alert.AlertType.INFORMATION);
        closeStageIfNeeded();
      } else {
        AlertHelper.showAlert("No changes were made.", Alert.AlertType.INFORMATION);
      }
    } catch (SQLException e) {
      AlertHelper.showAlert("Database error: " + e.getMessage(), Alert.AlertType.ERROR);
      Launcher.getLogger().error("Database error while saving client: {}", e.getMessage(), e);
    }
  }

  /**
   * Fills the PreparedStatement with client data from the form fields. Capitalizes certain fields
   * and converts the email to lowercase. Sets the current date for the date field.
   *
   * @param pstmt The PreparedStatement to fill with client data.
   * @throws SQLException If an SQL error occurs during setting the PreparedStatement values.
   */
  private void fillPreparedStatement(PreparedStatement pstmt) throws SQLException {
    pstmt.setString(1, capitalize(firstNameInput.getText().trim()));
    pstmt.setString(2, capitalize(lastNameInput.getText().trim()));
    pstmt.setString(3, emailInput.getText().trim().toLowerCase());
    pstmt.setString(4, phoneNumberInput.getText().trim());
    pstmt.setString(5, capitalize(referenceInput.getText().trim()));
    pstmt.setString(6, capitalize(citizenshipInput.getText().trim()));
    pstmt.setString(7, notesInput.getText().trim());
    pstmt.setDate(8, java.sql.Date.valueOf(LocalDate.now()));
    if (editingClient != null) {
      pstmt.setInt(9, editingClient.getId());
    }
  }

  /**
   * Closes the current stage if an existing client is being updated. If adding a new client, clears
   * the form instead. This method is called after successfully saving client data.
   */
  private void closeStageIfNeeded() {
    if (editingClient != null) {
      Stage stage = (Stage) firstNameInput.getScene().getWindow();
      stage.close();
    } else {
      clearForm();
    }
  }

  /**
   * Clears all input fields in the form. This method is typically called after successfully adding
   * a new client to reset the form for possible further entries.
   */
  private void clearForm() {
    firstNameInput.clear();
    lastNameInput.clear();
    emailInput.clear();
    phoneNumberInput.clear();
    referenceInput.clear();
    citizenshipInput.clear();
    notesInput.clear();
  }

  /**
   * Capitalizes the first letter of a given string and makes the rest of the letters lowercase.
   *
   * @param text The string to capitalize.
   * @return The capitalized string, or the original string if it's null or empty.
   */
  private String capitalize(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
  }
}
