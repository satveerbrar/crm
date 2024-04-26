package com.satveerbrar.crm;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {

    @FXML
    private ChoiceBox<String> applicationType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        applicationType.getItems().addAll("Visitor Visa", "Study Permit", "Work Permit", "Permanent Residency", "Citizenship");
    }
}
