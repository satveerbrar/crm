package com.satveerbrar.crm;

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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewApplicationsController implements Initializable {

    @FXML
    private TableView<ApplicationClient> applicationsTable;

    @FXML
    private TableColumn<ApplicationClient, String> colFirstName;

    @FXML
    private TableColumn<ApplicationClient, String> colLastName;

    @FXML
    private TableColumn<ApplicationClient, String> colApplicationType;

    @FXML
    private TableColumn<ApplicationClient, String> colApplicationStatus;

    @FXML
    private TableColumn<ApplicationClient, String> colSubmissionDate;

    @FXML
    private TableColumn<ApplicationClient, String> colPriority;

    @FXML
    private TableColumn<ApplicationClient, String> colNotes;

    @FXML
    private TableColumn<ApplicationClient, Void> colEdit;

    @FXML
    private TableColumn<ApplicationClient, Void> colDelete;

    @FXML
    private TextField searchField;

    ObservableList<ApplicationClient> applications = FXCollections.observableArrayList();

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
    }

    private void setupEditButton() {
        colEdit.setCellFactory(param -> new TableCell<ApplicationClient, Void>() {
            private final Button btn = new Button("Edit");

            {
                btn.setOnAction(event -> {
                    ApplicationClient applicationClient= getTableView().getItems().get(getIndex());
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

    private void setupDeleteButton() {
        colDelete.setCellFactory(param -> new TableCell<ApplicationClient, Void>() {
            private final Button btn = new Button("Delete");

            {
                btn.setOnAction(event -> {
                    ApplicationClient applicationClient = getTableView().getItems().get(getIndex());
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
            e.printStackTrace();
        }
    }

    private void deleteApplication(ApplicationClient applicationClient) {
        if (applicationClient == null || applicationClient.getId() == 0) {
            showAlert("No client selected to delete!", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this client?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                executeDelete(applicationClient);
            }
        });
    }

    private void executeDelete(ApplicationClient applicationClient) {
        String sql = "DELETE FROM applications WHERE application_id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, applicationClient.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Application deleted successfully!", Alert.AlertType.INFORMATION);
                applicationsTable.getItems().remove(applicationClient);
            } else {
                showAlert("No application was deleted. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Error while deleting the application: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupSearch(){
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterApplications(newVal));
    }

    private void filterApplications(String searchText){
        if(searchText == null || searchText.isEmpty()){
            applicationsTable.setItems(applications);
        }
        else{
            ObservableList<ApplicationClient> filteredList = FXCollections.observableArrayList();
            for (ApplicationClient applicationClient : applications){
                if(applicationClient.matchesSearch(searchText)){
                    filteredList.add(applicationClient);
                }
                applicationsTable.setItems(filteredList);
            }
        }
    }

    private void loadApplicationData() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection conn = dbConnection.getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT a.*, c.FIRST_NAME, c.LAST_NAME FROM applications a JOIN clients c ON a.client_id = c.client_id")) {

            while (rs.next()) {
                applications.add(new ApplicationClient(
                        rs.getInt("APPLICATION_ID"),
                        rs.getString("FIRST_NAME"),
                        rs.getString("LAST_NAME"),
                        rs.getString("APPLICATION_TYPE"),
                        rs.getString("APPLICATION_STATUS"),
                        rs.getString("SUBMISSION_DATE"),
                        rs.getString("PRIORITY"),
                        rs.getString("NOTES")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error fetching clients: " + e.getMessage());
        }
        applicationsTable.setItems(applications);
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

