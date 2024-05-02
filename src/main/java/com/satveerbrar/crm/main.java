package com.satveerbrar.crm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {

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
