package com.satveerbrar.crm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ViewClientsController implements Initializable {
    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, String> colFirstName;
    @FXML
    private TableColumn<Client, String> colLastName;
    @FXML
    private TableColumn<Client, String> colEmail;
    @FXML
    private TableColumn<Client, String> colPhoneNumber;
    @FXML
    private TableColumn<Client, String> colReference;
    @FXML
    private TableColumn<Client, String> colCitizenship;
    @FXML
    private TableColumn<Client, String> colDate;
    @FXML
    private TableColumn<Client, String> colNotes;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("PhoneNumber"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("Reference"));
        colCitizenship.setCellValueFactory(new PropertyValueFactory<>("Citizenship"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("Notes"));

        loadClientData();
    }

    private void loadClientData() {
        ObservableList<Client> clients = FXCollections.observableArrayList();
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection conn = dbConnection.getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {

            while (rs.next()) {
                clients.add(new Client(
                        rs.getString("FIRST_NAME"),
                        rs.getString("LAST_NAME"),
                        rs.getString("EMAIL"),
                        rs.getString("PHONE_NUMBER"),
                        rs.getString("REFERENCE"),
                        rs.getString("CITIZENSHIP"),
                        rs.getString("DATE"),
                        rs.getString("NOTES")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error fetching clients: " + e.getMessage());
        }
        clientsTable.setItems(clients);
    }
}
