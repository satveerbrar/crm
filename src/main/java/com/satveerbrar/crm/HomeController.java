package com.satveerbrar.crm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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
    private Label homeNavButton;
    @FXML
    private Label addClientNavButton;
    @FXML
    private Label addApplicationsNavButton;
    @FXML
    private Label viewApplicationNavButton;
    @FXML
    private Label viewClientsNavButton;
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




    public void loadPage(String page) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(page + ".fxml"));
            Parent root = loader.load();
            Launcher.getLogger().info("Loading page: {}", page);
            bp.setCenter(root);
        } catch (IOException e) {
            Launcher.getLogger().error("Failed to load page {}: {}", page, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void homePage(MouseEvent event){
        Launcher.getLogger().info("Navigating to home page");
        loadDashboards();
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
        ButtonUtils.setHoverCursor(homeNavButton);
        ButtonUtils.setHoverCursor(addClientNavButton);
        ButtonUtils.setHoverCursor(addApplicationsNavButton);
        ButtonUtils.setHoverCursor(viewApplicationNavButton);
        ButtonUtils.setHoverCursor(viewClientsNavButton);
        ButtonUtils.setHoverCursor(homeNavButton);
        loadDashboards();
        Launcher.getLogger().info("HomeController initialized");
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
            Launcher.getLogger().error("Error executing SQL query: {}", e.getMessage(), e);
        }
    }

    private void updateLabelWithCount(Connection conn, String query, Label label) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                label.setText(String.valueOf(count));
            }
            Launcher.getLogger().info("Label update successfully {}", label);
        } catch (SQLException e) {
            Launcher.getLogger().error("Error executing SQL query: {}", e.getMessage(), e);
            label.setText("Error");
        }
    }


}
