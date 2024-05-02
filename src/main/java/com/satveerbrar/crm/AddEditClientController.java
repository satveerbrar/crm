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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    if (editingClient != null) {
      populateFormData();
    }
    ButtonUtils.setHoverCursor(clientSubmitButton);
    Launcher.getLogger().info("AddEditClientController initialized.");
  }

  public void setEditingClient(Client client) {
    this.editingClient = client;
    if (client != null) {
      populateFormData();
      headerLabel.setText("Edit Existing Client");
    } else {
      headerLabel.setText("Add New Client");
    }
  }

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

  public void submitForm() {
    if (validateInputs()) {
      saveClientData();
    }
  }

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

  private void closeStageIfNeeded() {
    if (editingClient != null) {
      Stage stage = (Stage) firstNameInput.getScene().getWindow();
      stage.close();
    } else {
      clearForm();
    }
  }

  private void clearForm() {
    firstNameInput.clear();
    lastNameInput.clear();
    emailInput.clear();
    phoneNumberInput.clear();
    referenceInput.clear();
    citizenshipInput.clear();
    notesInput.clear();
  }

  private String capitalize(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return Character.toUpperCase(text.charAt(0)) + text.substring(1).toLowerCase();
  }
}
