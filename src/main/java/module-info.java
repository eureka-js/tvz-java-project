module com.example.juricastanic_projektnizadatak {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;


    opens main to javafx.fxml;
    exports main;
    opens util.thread to javafx.fxml;
    exports util.thread;
}