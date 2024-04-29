package com.satveerbrar.crm;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddClientController implements Initializable {

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

    @FXML
    private ChoiceBox<String> applicationTypeChoiceBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        applicationTypeChoiceBox.getItems().addAll("Visitor Visa", "Study Permit", "Work Permit", "Permanent Residency", "Citizenship", "Other");
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
        }else if(applicationTypeChoiceBox.getValue() == null){
            showAlert("Please select application type", Alert.AlertType.ERROR);
            applicationTypeChoiceBox.requestFocus();
            return false;
        }
        return true;
    }



    public void submitForm() {
        if (isInputValid()) {
            if (validateEmail(emailInput.getText()) && validatePhoneNumber(phoneNumberInput.getText())) {
                saveToDatabase();
            }
        } else {
            System.out.println("Validation failed");
        }
    }

    private void saveToDatabase(){
        DatabaseConnection dbConnection = new DatabaseConnection();
        String sql = "INSERT INTO clients (first_name, last_name, email, phone_number, reference, citizenship, notes, application_type, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, capitalize(firstNameInput.getText().trim()));
            pstmt.setString(2, capitalize(lastNameInput.getText().trim()));
            pstmt.setString(3, emailInput.getText().trim().toLowerCase());
            pstmt.setString(4, phoneNumberInput.getText().trim());
            pstmt.setString(5, capitalize(referenceInput.getText().trim()));
            pstmt.setString(6, capitalize(citizenshipInput.getText().trim()));
            pstmt.setString(7, notesInput.getText().trim());
            pstmt.setString(8, applicationTypeChoiceBox.getValue());
            pstmt.setDate(9, java.sql.Date.valueOf(LocalDate.now()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Client added successfully!", Alert.AlertType.INFORMATION);
                clearForm();
            } else {
                showAlert("No customer was added, please try again.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            showAlert("Error while saving to the database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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
        applicationTypeChoiceBox.setValue(null);
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
