package com.satveerbrar.crm;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
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

public class ViewClientsController implements Initializable {
  private final ObservableList<Client> clients = FXCollections.observableArrayList();
  @FXML private TableView<Client> clientsTable;
  @FXML private TableColumn<Client, Integer> colClientId;
  @FXML
  private TableColumn<Client, String> colFirstName,
      colLastName,
      colEmail,
      colPhoneNumber,
      colReference,
      colCitizenship,
      colDate,
      colNotes;
  @FXML private TableColumn<Client, Void> colEdit, colDelete;
  @FXML private TextField searchField;

  /**
   * Initializes the controller by configuring table columns, setting up button functionalities, and
   * loading initial client data into the table. This method is called automatically after the FXML
   * fields have been injected.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    configureTableColumns();
    setupSearch();
    loadClientData();
    setupEditButton();
    setupDeleteButton();

    Launcher.getLogger().info("ViewClientsController initialized.");
  }

  /**
   * Configures the table columns for the TableView. Each column is set up to display specific
   * attributes of the Client objects, such as first name, last name, email, etc.
   */
  private void configureTableColumns() {
    colClientId.setCellValueFactory(new PropertyValueFactory<>("Id"));
    colFirstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
    colLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
    colEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
    colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("PhoneNumber"));
    colReference.setCellValueFactory(new PropertyValueFactory<>("Reference"));
    colCitizenship.setCellValueFactory(new PropertyValueFactory<>("Citizenship"));
    colDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
    colNotes.setCellValueFactory(new PropertyValueFactory<>("Notes"));
  }

  /**
   * Sets up the edit button for each row in the clients table. This button allows the user to edit
   * the selected client's details in a separate editing form.
   */
  private void setupEditButton() {
    colEdit.setCellFactory(
        param ->
            new TableCell<Client, Void>() {
              private final Button btn = new Button("Edit");

              {
                btn.setOnAction(
                    event -> {
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

  /**
   * Configures the delete button for each row in the clients table. This button enables the
   * deletion of the selected client, with a confirmation prompt before proceeding.
   */
  private void setupDeleteButton() {
    colDelete.setCellFactory(
        param ->
            new TableCell<Client, Void>() {
              private final Button btn = new Button("Delete");

              {
                btn.setOnAction(
                    event -> {
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

  /**
   * Opens the client editing form for the selected client. The form is pre-populated with the
   * client's existing data, allowing for modifications.
   *
   * @param client The client object to be edited.
   */
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
      Launcher.getLogger().error("Error while editing client: {}", e.getMessage());
    }
  }

  /**
   * Initiates the deletion process for a selected client by presenting a confirmation alert. If
   * confirmed, the client is removed from the database and the table.
   *
   * @param client The client object to be deleted.
   */
  private void deleteClient(Client client) {
    if (client == null || client.getId() == 0) {
      AlertHelper.showAlert("No client selected to delete!", Alert.AlertType.ERROR);
      Launcher.getLogger().warn("No client selected to delete.");
      return;
    }

    Alert confirmationAlert =
        new Alert(
            Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete this client?",
            ButtonType.YES,
            ButtonType.NO);
    confirmationAlert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.YES) {
                executeDelete(client);
              }
            });
  }

  /**
   * Executes the deletion of a client from the database. If successful, also removes the client
   * from the table view.
   *
   * @param client The client object to be deleted from the database.
   */
  private void executeDelete(Client client) {
    String sql = "DELETE FROM clients WHERE client_id = ?";

    try (Connection conn = new DatabaseConnection().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, client.getId());
      int affectedRows = pstmt.executeUpdate();
      if (affectedRows > 0) {

        AlertHelper.showAlert("Client deleted successfully!", Alert.AlertType.INFORMATION);
        clientsTable.getItems().remove(client);
        Launcher.getLogger().info("Client deleted successfully.");
      } else {
        AlertHelper.showAlert("No client was deleted. Please try again.", Alert.AlertType.ERROR);
        Launcher.getLogger().warn("No client was deleted.");
      }
    } catch (SQLException e) {
      AlertHelper.showAlert(
          "Error while deleting the client: " + e.getMessage(), Alert.AlertType.ERROR);
      Launcher.getLogger().error("Error while deleting the client: {}", e.getMessage());
    }
  }

  /**
   * Sets up a listener on the search field to dynamically filter the displayed clients in the table
   * as the user types.
   */
  private void setupSearch() {
    searchField.textProperty().addListener((obs, oldVal, newVal) -> filterClients(newVal));
  }

  /**
   * Filters the clients displayed in the table based on the search text input by the user. If the
   * search text is empty, all clients are displayed. Otherwise, only clients matching the search
   * criteria are shown.
   *
   * @param searchText The text used to filter the clients displayed.
   */
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

  /**
   * Loads all client data from the database into the clients table. Each row in the table
   * represents a client, displaying details such as name, email, phone number, etc.
   */
  private void loadClientData() {
    DatabaseConnection dbConnection = new DatabaseConnection();
    Connection conn = dbConnection.getConnection();

    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {

      while (rs.next()) {
        clients.add(
            new Client(
                rs.getInt("CLIENT_ID"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getString("EMAIL"),
                rs.getString("PHONE_NUMBER"),
                rs.getString("REFERENCE"),
                rs.getString("CITIZENSHIP"),
                rs.getDate("DATE").toString(),
                rs.getString("NOTES")));
      }
      Launcher.getLogger().info("Client data loaded successfully.");
    } catch (Exception e) {
      System.out.println("Error fetching clients: " + e.getMessage());
      Launcher.getLogger().error("Error fetching clients: {}", e.getMessage());
    }
    clientsTable.setItems(clients);
  }
}
