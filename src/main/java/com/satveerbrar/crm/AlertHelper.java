package com.satveerbrar.crm;

import javafx.scene.control.Alert;

/**
 * Utility class for displaying alerts in the application. This class simplifies the process of
 * creating and showing alerts by providing a static method that handles alert configuration.
 */
public class AlertHelper {

  /**
   * Displays an alert with the specified message and type. The alert's title is set based on the
   * alert type, and it waits for the user to dismiss it before returning.
   *
   * @param message The message to be displayed in the alert body.
   * @param alertType The type of the alert (e.g., INFORMATION, ERROR), which determines the icon
   *     and default title.
   */
  public static void showAlert(String message, Alert.AlertType alertType) {
    Alert alert = new Alert(alertType);
    alert.setTitle(alertType.toString());
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
