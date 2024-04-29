package com.satveerbrar.crm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddApplicationController implements Initializable {

    @FXML
    private TextField clientId;
    @FXML
    private ChoiceBox<String> applicationTypeChoiceBox;
    @FXML
    private ChoiceBox<String> applicationStatusChoiceBox;
    @FXML
    private ChoiceBox<String> priorityChoiceBox;
    @FXML
    private TextArea notesInput;

    @FXML
    public void submitForm(ActionEvent actionEvent) {
        if(validateInputs()){
            if(saveToDatabase()){
                showAlert("Application saved successfully.", Alert.AlertType.INFORMATION);
                clearForm();
            }
        } else {
            System.out.println("Validation failed");
        }
    }

    public boolean validateClientId(String clientId){
        int clientIdInt;

        if(clientId.isEmpty()){
            showAlert("Please enter client_id", Alert.AlertType.ERROR);
            return false;
        }

        try{
            clientIdInt = Integer.parseInt(clientId);
        }catch (NumberFormatException e){
            System.out.println("Error while converting client_id to int" + e.getMessage());
            showAlert("Please enter valid client_id", Alert.AlertType.ERROR);
            return false;
        }

        DatabaseConnection dbConnection = new DatabaseConnection();
        String sql = "Select count(*) from clients WHERE client_id = ?";
        try(Connection conn = dbConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, clientIdInt);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    return true;
                } else {
                    showAlert("No client found with the provided ID.", Alert.AlertType.ERROR);
                    return false;
                }
            }

        }catch (SQLException e){
            System.out.println("SQL Error: " + e.getMessage());
            showAlert("Error while fetching client_id: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeChoiceBoxes();
    }

    private boolean validateInputs() {
        return validateClientId(clientId.getText().trim()) &&
                validateChoiceBox(applicationTypeChoiceBox, "Select Application Type", "application type") &&
                validateChoiceBox(applicationStatusChoiceBox, "Select Status", "application status") &&
                validateChoiceBox(priorityChoiceBox, "Select Priority", "priority");
    }

    private boolean validateChoiceBox(ChoiceBox<String> choiceBox, String defaultValue, String fieldName) {
        if (choiceBox.getValue() == null || choiceBox.getValue().equals(defaultValue)) {
            showAlert("Please select " + fieldName, Alert.AlertType.ERROR);
            choiceBox.requestFocus();
            return false;
        }
        return true;
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void clearForm() {
        clientId.clear();
        notesInput.clear();
        applicationTypeChoiceBox.setValue("Select Application Type");
        applicationStatusChoiceBox.setValue("Select Status");
        priorityChoiceBox.setValue("Select Priority");
    }

    private void initializeChoiceBoxes() {
        applicationTypeChoiceBox.getItems().addAll("Visitor Visa", "Study Permit", "Work Permit", "Permanent Residency", "Citizenship", "Other");
        applicationTypeChoiceBox.setValue("Select Application Type");

        applicationStatusChoiceBox.getItems().addAll("Not Started", "In progress", "Approved", "Rejected");
        applicationStatusChoiceBox.setValue("Select Status");

        priorityChoiceBox.getItems().addAll("Low", "Medium", "High");
        priorityChoiceBox.setValue("Select Priority");
    }
    private boolean saveToDatabase() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        String sql = "INSERT INTO applications (application_type, application_status, priority, submission_date, notes, client_id ) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, applicationTypeChoiceBox.getValue());
            pstmt.setString(2, applicationStatusChoiceBox.getValue());
            pstmt.setString(3, priorityChoiceBox.getValue());
            pstmt.setDate(4,java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setString(5, notesInput.getText());
            pstmt.setInt(6, Integer.parseInt(clientId.getText().trim()));

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            showAlert("Error while saving to database: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        } catch (NumberFormatException e) {
            showAlert("Invalid Client ID format.", Alert.AlertType.ERROR);
            return false;
        }
    }
}
