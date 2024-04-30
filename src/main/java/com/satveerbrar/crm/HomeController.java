package com.satveerbrar.crm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;
    @FXML
    private Label totalClientsLabel;
    @FXML
    private Label totalApplicationsLabel;
    @FXML
    private Label totalNotStartedLabel;
    @FXML
    private Label totalInProgressLabel;
    @FXML
    private Label totalApprovedLabel;
    @FXML
    private Label totalRejectedLabel;




    public void loadPage(String page)  {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(page + ".fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bp.setCenter(root);

    }

    @FXML
    private void homePage(MouseEvent event){
        bp.setCenter(ap);
    }

    @FXML
    private void addClient(MouseEvent event){
        loadPage("addClient");
    }

    @FXML
    private void addApplication(MouseEvent event){
        loadPage("addApplication");
    }

    @FXML
    private void viewApplications(MouseEvent event){
        loadPage("viewApplications");
    }

    @FXML
    private void viewClients(MouseEvent event){
        loadPage("viewClients");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboards();
    }

    private void loadDashboards() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        try (Connection conn = dbConnection.getConnection()) {
            updateLabelWithCount(conn, "SELECT COUNT(*) FROM clients", totalClientsLabel);
            updateLabelWithCount(conn, "SELECT COUNT(*) FROM applications", totalApplicationsLabel);
            updateLabelWithCount(conn, "SELECT COUNT(*) FROM applications WHERE application_status = 'Not Started'", totalNotStartedLabel);
            updateLabelWithCount(conn, "SELECT COUNT(*) FROM applications WHERE application_status = 'In Progress'", totalInProgressLabel);
            updateLabelWithCount(conn, "SELECT COUNT(*) FROM applications WHERE application_status = 'Approved'", totalApprovedLabel);
            updateLabelWithCount(conn, "SELECT COUNT(*) FROM applications WHERE application_status = 'Rejected'", totalRejectedLabel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLabelWithCount(Connection conn, String query, Label label) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                label.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            label.setText("Error");
        }
    }


}
