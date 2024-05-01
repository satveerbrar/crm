package com.satveerbrar.crm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddEditApplicationController implements Initializable {
    @FXML
    private TextField clientId;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private ChoiceBox<String> applicationTypeChoiceBox;
    @FXML
    private ChoiceBox<String> applicationStatusChoiceBox;
    @FXML
    private ChoiceBox<String> priorityChoiceBox;
    @FXML
    private TextArea notesInput;
    @FXML
    private Label headerLabel;
    @FXML
    private Button applicationSubmitButton;

    private ApplicationClient editingApplicationClient;

    @FXML
    public void submitForm(ActionEvent actionEvent) {
        if(validateInputs()){
            if(editingApplicationClient == null){
                saveToDatabase(true);
            }else{
                saveToDatabase(false);
            }

        } else {
            System.out.println("Validation failed");
        }
    }

    public void setEditingApplicationClient(ApplicationClient applicationClient) {
        this.editingApplicationClient = applicationClient;
        if (applicationClient != null) {
            initializeFormWithClientData();
            headerLabel.setText("Edit Existing Client");
        } else {
            headerLabel.setText("Add New Client");
        }
    }

    private void initializeFormWithClientData() {
        clientId.setDisable(true);
        applicationTypeChoiceBox.setValue(editingApplicationClient.getApplicationType());
        applicationStatusChoiceBox.setValue(editingApplicationClient.getApplicationStatus());
        priorityChoiceBox.setValue(editingApplicationClient.getPriority());
        notesInput.setText(editingApplicationClient.getNotes());
    }

    public boolean validateClientId(String clientId){

        if(editingApplicationClient != null){
            return true;
        }

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
        setupClientIdChangeListener();
        ButtonUtils.setHoverCursor(applicationSubmitButton);
    }

    private void setupClientIdChangeListener() {
        clientId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                updatePromptTexts(newValue.trim());
            }
        });
    }

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
            System.out.println("SQL Error: " + e.getMessage());
            showAlert("Error fetching client details: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (NumberFormatException e) {
            showAlert("Invalid Client ID format.", Alert.AlertType.ERROR);
        }
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
    private void saveToDatabase(boolean isNew) {
        String sql;
        if(isNew){
            sql = "INSERT INTO applications (application_type, application_status, priority, submission_date, notes, client_id ) VALUES (?, ?, ?, ?, ?, ?)";
        }
        else{
            sql = "UPDATE applications SET application_type = ?, application_status = ?, priority = ?, submission_date = ?, notes = ? WHERE application_id = ?";
        }
        DatabaseConnection dbConnection = new DatabaseConnection();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, applicationTypeChoiceBox.getValue());
            pstmt.setString(2, applicationStatusChoiceBox.getValue());
            pstmt.setString(3, priorityChoiceBox.getValue());
            pstmt.setDate(4,java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setString(5, notesInput.getText());
            if(!isNew){
                pstmt.setInt(6, editingApplicationClient.getId());
            }else{
                pstmt.setInt(6, Integer.parseInt(clientId.getText().trim()));
            }


            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Application " + (isNew ? "added" : "updated") + " successfully!", Alert.AlertType.INFORMATION);
                if (!isNew) {
                    closeStage();
                } else {
                    clearForm();
                }
            } else {
                showAlert("No changes were made.", Alert.AlertType.ERROR);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            showAlert("Error while saving to database: " + e.getMessage(), Alert.AlertType.ERROR);

        } catch (NumberFormatException e) {
            showAlert("Invalid Client ID format.", Alert.AlertType.ERROR);
        }
    }

    private void closeStage() {
        Stage stage = (Stage) applicationTypeChoiceBox.getScene().getWindow();
        stage.close();
    }
}
