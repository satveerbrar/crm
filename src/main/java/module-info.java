module com.satveerbrar.crm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;


    opens com.satveerbrar.crm to javafx.fxml;
    exports com.satveerbrar.crm;
}