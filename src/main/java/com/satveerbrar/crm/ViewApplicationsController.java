package com.satveerbrar.crm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
}

