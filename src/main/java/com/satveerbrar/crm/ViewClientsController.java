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

public class ViewClientsController implements Initializable {
    @FXML
    private TableView<Client> clientsTable;

    @FXML
    private TableColumn<Client, Integer> colClientId;
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
    @FXML
    private TableColumn<Client, Void> colEdit;
    @FXML
    private TableColumn<Client, Void> colDelete;
    @FXML
    private TextField searchField;

    ObservableList<Client> clients = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colClientId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("PhoneNumber"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("Reference"));
        colCitizenship.setCellValueFactory(new PropertyValueFactory<>("Citizenship"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("Notes"));

        setupSearch();
        loadClientData();
        setupEditButton();
        setupDeleteButton();
    }

    private void setupEditButton() {
        colEdit.setCellFactory(param -> new TableCell<Client, Void>() {
            private final Button btn = new Button("Edit");

            {
                btn.setOnAction(event -> {
                    Client client = getTableView().getItems().get(getIndex());
                    editClient(client);
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
        colDelete.setCellFactory(param -> new TableCell<Client, Void>() {
            private final Button btn = new Button("Delete");

            {
                btn.setOnAction(event -> {
                    Client client = getTableView().getItems().get(getIndex());
                    deleteClient(client);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void editClient(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addClient.fxml"));
            Parent root = loader.load();
            AddEditClientController controller = loader.getController();
            controller.setEditingClient(client);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Client");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteClient(Client client) {
        if (client == null || client.getId() == 0) {
            showAlert("No client selected to delete!", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this client?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                executeDelete(client);
            }
        });
    }

    private void executeDelete(Client client) {
        String sql = "DELETE FROM clients WHERE client_id = ?";

        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, client.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Client deleted successfully!", Alert.AlertType.INFORMATION);
                clientsTable.getItems().remove(client);
            } else {
                showAlert("No client was deleted. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Error while deleting the client: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterClients(newVal));
    }

    private void filterClients(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            clientsTable.setItems(clients);
        } else {
            ObservableList<Client> filteredList = FXCollections.observableArrayList();
            for (Client client : clients) {
                if (client.matchesSearch(searchText)) {
                    filteredList.add(client);
                }
            }
            clientsTable.setItems(filteredList);
        }
    }



    private void loadClientData() {
        DatabaseConnection dbConnection = new DatabaseConnection();
        Connection conn = dbConnection.getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {

            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("CLIENT_ID"),
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

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType.toString());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
