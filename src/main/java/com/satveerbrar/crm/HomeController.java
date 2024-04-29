package com.satveerbrar.crm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class HomeController {

    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;


    public void loadPage(String page)  {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(page + ".fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bp.setCenter(root);

    }

    @FXML
    private void homePage(MouseEvent event){
        bp.setCenter(ap);
    }

    @FXML
    private void addClient(MouseEvent event){
        loadPage("addClient");
    }

    @FXML
    private void addApplication(MouseEvent event){
        loadPage("addApplication");
    }

    @FXML
    private void viewApplications(MouseEvent event){
        loadPage("viewApplications");
    }

    @FXML
    private void viewClients(MouseEvent event){
        loadPage("viewClients");
    }


}
