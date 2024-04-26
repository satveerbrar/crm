package com.satveerbrar.crm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SideBarController {

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
    private void addCustomer(MouseEvent event){
        loadPage("addCustomer");
    }

    @FXML
    private void editCustomer(MouseEvent event){
        loadPage("editCustomer");
    }

    @FXML
    private void deleteCustomer(MouseEvent event){
        loadPage("deleteCustomer");
    }

    @FXML
    private void viewCustomers(MouseEvent event){
        loadPage("viewCustomers");
    }


}
