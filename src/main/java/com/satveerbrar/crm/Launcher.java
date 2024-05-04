package com.satveerbrar.crm;

import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class for launching the CRM application. This class initializes the database, sets up
 * logging, and starts the JavaFX application.
 */
public class Launcher {

  private static final Logger logger = LogManager.getLogger(Launcher.class);

  /**
   * Provides access to the centralized logger for the application.
   *
   * @return The configured Logger instance used throughout the application.
   */
  public static Logger getLogger() {
    return logger;
  }

  /**
   * The main entry point of the application. This method initializes the database, logs the start
   * of the application, and launches the JavaFX user interface.
   *
   * @param args The command-line arguments passed to the application.
   */
  public static void main(String[] args) {

    logger.info("Starting application");
    DatabaseInitializer.initializeDatabase();
    Application.launch(main.class, args);
  }
}
