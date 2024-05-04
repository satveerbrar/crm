package com.satveerbrar.crm;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the login process for the CRM application. This class handles user input for login
 * credentials, validates them against the database, and manages navigation based on authentication
 * results.
 */
public class LoginController implements Initializable {

  @FXML private TextField usernameInput;
  @FXML private PasswordField passwordInput;
  @FXML private Label invalidLoginMessageLabel;
  @FXML private Button loginButton;

  /**
   * Initializes the controller class. Sets up necessary UI components such as button cursors and
   * logs the initialization.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    ButtonUtils.setHoverCursor(loginButton);
    Launcher.getLogger().info("LoginController initialized");
  }

  /**
   * Handles the action triggered by pressing the login button. Validates that the username and
   * password fields are not empty, logs attempts, and calls the method to validate credentials
   * against the database.
   *
   * @param event The action event triggered when the login button is pressed.
   */
  @FXML
  public void handleLoginButtonAction(ActionEvent event) {
    Launcher.getLogger().info("Login button clicked");
    invalidLoginMessageLabel.setText("Invalid Login. Please try again");

    if (usernameInput.getText().isBlank() && passwordInput.getText().isBlank()) {
      Launcher.getLogger().warn("Invalid login attempt. username and password blank");
      invalidLoginMessageLabel.setText("Please enter username and password");
    } else if (usernameInput.getText().isBlank()) {
      Launcher.getLogger().warn("Invalid login attempt. username blank");
      invalidLoginMessageLabel.setText("Please enter username");
    } else if (passwordInput.getText().isBlank()) {
      Launcher.getLogger().warn("Invalid login attempt. password blank");
      invalidLoginMessageLabel.setText("Please enter password");
    } else {
      validateLogin();
    }
  }

  /**
   * Validates the user's login credentials against the database. If the credentials are valid, the
   * user is redirected to the home page. Otherwise, displays an error message on the login form.
   */
  public void validateLogin() {
    DatabaseConnection connectNow = new DatabaseConnection();
    Connection connectDb = connectNow.getConnection();

    String verifyLogin = "SELECT count(1) FROM user_accounts WHERE username = ? AND password = ?";

    try (PreparedStatement statement = connectDb.prepareStatement(verifyLogin)) {
      statement.setString(1, usernameInput.getText());
      statement.setString(2, passwordInput.getText());

      ResultSet queryResult = statement.executeQuery();

      if (queryResult.next() && queryResult.getInt(1) == 1) {
        Launcher.getLogger().info("Login validated for user: {}", usernameInput.getText());
        transitionToHomePage();
      } else {
        Launcher.getLogger().warn("Invalid login attempt for user: {}", usernameInput.getText());
        invalidLoginMessageLabel.setText("Invalid Login! Try again");
      }
    } catch (Exception e) {
      Launcher.getLogger()
          .error("Exception occurred during login validation: {}", e.getMessage(), e);
    }
  }

  /**
   * Transitions the user to the home page upon successful login. This method loads the home view
   * and displays it in a new stage.
   *
   * @throws IOException If there is an error loading the home page FXML.
   */
  private void transitionToHomePage() throws IOException {
    loginButton.getScene().getWindow().hide();
    Launcher.getLogger().info("Loading home.fxml");
    FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("home.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1200, 720);
    Stage stage = new Stage();
    stage.setTitle("Home");
    stage.setScene(scene);
    stage.show();
  }
}
