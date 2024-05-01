module com.satveerbrar.crm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.apache.logging.log4j;


    opens com.satveerbrar.crm to javafx.fxml;
    exports com.satveerbrar.crm;
}