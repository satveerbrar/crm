package com.satveerbrar.crm;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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


public class LoginController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO
    }

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Label invalidLoginMessageLabel;

    @FXML
    private Button loginButton;

    @FXML
    public void handleLoginButtonAction(ActionEvent event) {
        invalidLoginMessageLabel.setText("Invalid Login. Please try again");

        if(usernameInput.getText().isBlank() && passwordInput.getText().isBlank()){
            invalidLoginMessageLabel.setText("Please enter username and password");
        }else if(usernameInput.getText().isBlank()){
           invalidLoginMessageLabel.setText("Please enter username");
        }
        else if(passwordInput.getText().isBlank()){
            invalidLoginMessageLabel.setText("Please enter password");
        }
        else{
            validateLogin();
        }
    }

    public void validateLogin(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDb = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM user_account WHERE username = '" + usernameInput.getText() + "' AND password = '" + passwordInput.getText() + "' ";

        try{
            Statement statement = connectDb.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while (queryResult.next()){
                if(queryResult.getInt(1) == 1){
                    loginButton.getScene().getWindow().hide();
                    FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("sidebar.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 1200, 720);
                    Stage stage = new Stage();
                    stage.setTitle("Home");
                    stage.setScene(scene);
                    stage.show();
                }
                else{
                    invalidLoginMessageLabel.setText("Invalid Login! Try again");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            e.getCause();
        }
    }

}
