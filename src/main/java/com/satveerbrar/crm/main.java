package com.satveerbrar.crm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class that extends {@link Application} to start the JavaFX application. This class is
 * responsible for setting up the primary stage and loading the initial scene, which is the login
 * screen for the CRM application.
 */
public class main extends Application {

  /**
   * Starts the application by setting up the primary stage and loading the initial scene. This
   * method is called after the application is launched from the {@link Launcher} main method.
   *
   * @param primaryStage The primary stage for this application, onto which the application scene
   *     can be set.
   * @throws Exception if there is an issue loading the FXML file or setting up the scene.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    Launcher.getLogger().info("Loading login screen");
    FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("login.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 765, 601);
    primaryStage.setTitle("ATG Immigration");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
