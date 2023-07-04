module com.app.threadapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;


    opens com.app.threadapp to javafx.fxml;
    exports com.app.threadapp;
}