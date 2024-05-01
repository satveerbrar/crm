package com.satveerbrar.crm;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddEditClientController implements Initializable {

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField phoneNumberInput;

    @FXML
    private TextField referenceInput;

    @FXML
    private TextField citizenshipInput;

    @FXML
    private TextArea notesInput;

    private Client editingClient;

    @FXML
    private Label headerLabel;

    @FXML
    private Button clientSubmitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (editingClient != null) {
            initializeFormWithClientData();
        }
        ButtonUtils.setHoverCursor(clientSubmitButton);
    }

    public void setEditingClient(Client client) {
        this.editingClient = client;
        if (client != null) {
            initializeFormWithClientData();
            headerLabel.setText("Edit Existing Client");
        } else {
            headerLabel.setText("Add New Client");
        }
    }

    private void initializeFormWithClientData() {
        firstNameInput.setText(editingClient.getFirstName());
        lastNameInput.setText(editingClient.getLastName());
        emailInput.setText(editingClient.getEmail());
        phoneNumberInput.setText(editingClient.getPhoneNumber());
        referenceInput.setText(editingClient.getReference());
        citizenshipInput.setText(editingClient.getCitizenship());
        notesInput.setText(editingClient.getNotes());
    }

    private boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if(!email.matches(emailRegex)){
            showAlert("Invalid email", Alert.AlertType.ERROR);
            emailInput.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+?\\d{10,13}$";
        if(!phoneNumber.matches(phoneRegex)){
            showAlert("Invalid Phone number", Alert.AlertType.ERROR);
            phoneNumberInput.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isInputValid() {
        if (firstNameInput.getText().trim().isEmpty()){
            showAlert("Please enter first Name", Alert.AlertType.ERROR);
            firstNameInput.requestFocus();
            return false;
        }else if(lastNameInput.getText().trim().isEmpty()){
            showAlert("Please enter last Name", Alert.AlertType.ERROR);
            lastNameInput.requestFocus();
            return false;
        }else if(emailInput.getText().trim().isEmpty()){
            showAlert("Please enter email", Alert.AlertType.ERROR);
            emailInput.requestFocus();
            return false;
        }else if(phoneNumberInput.getText().trim().isEmpty()){
            showAlert("Please enter phone number", Alert.AlertType.ERROR);
            phoneNumberInput.requestFocus();
            return false;
        }
        return true;
    }



    public void submitForm() {
        if (isInputValid() && validateEmail(emailInput.getText()) && validatePhoneNumber(phoneNumberInput.getText())) {
            if (editingClient == null) {
                saveToDatabase(true);
            } else {
                saveToDatabase(false);
            }
        }
    }

    private void saveToDatabase(boolean isNew) {
        String sql;
        if (isNew) {
            sql = "INSERT INTO clients (first_name, last_name, email, phone_number, reference, citizenship, notes, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE clients SET first_name = ?, last_name = ?, email = ?, phone_number = ?, reference = ?, citizenship = ?, notes = ?, date = ? WHERE client_id = ?";
        }

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, capitalize(firstNameInput.getText().trim()));
            pstmt.setString(2, capitalize(lastNameInput.getText().trim()));
            pstmt.setString(3, emailInput.getText().trim().toLowerCase());
            pstmt.setString(4, phoneNumberInput.getText().trim());
            pstmt.setString(5, capitalize(referenceInput.getText().trim()));
            pstmt.setString(6, capitalize(citizenshipInput.getText().trim()));
            pstmt.setString(7, notesInput.getText().trim());
            pstmt.setDate(8, java.sql.Date.valueOf(LocalDate.now()));

            if (!isNew) {
                pstmt.setInt(9, editingClient.getId()); // Assuming Client has an ID field
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Client " + (isNew ? "added" : "updated") + " successfully!", Alert.AlertType.INFORMATION);
                if (!isNew) {
                    closeStage();
                } else {
                    clearForm();
                }
            } else {
                showAlert("No changes were made.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Database error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void closeStage() {
        Stage stage = (Stage) firstNameInput.getScene().getWindow();
        stage.close();
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    private void clearForm() {
        firstNameInput.setText("");
        lastNameInput.setText("");
        emailInput.setText("");
        phoneNumberInput.setText("");
        referenceInput.setText("");
        citizenshipInput.setText("");
        notesInput.setText("");
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
