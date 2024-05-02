package com.satveerbrar.crm;

import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Launcher {

  private static final Logger logger = LogManager.getLogger(Launcher.class);

  public static Logger getLogger() {
    return logger;
  }

  public static void main(String[] args) {

    logger.info("Starting application");
    DatabaseInitializer.initializeDatabase();
    Application.launch(main.class, args);
  }
}
