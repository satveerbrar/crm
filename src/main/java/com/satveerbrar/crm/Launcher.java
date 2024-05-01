package com.satveerbrar.crm;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();
        Application.launch(main.class, args);
    }
}
