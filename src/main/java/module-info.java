module com.cervinschi.marin.javafx.briscola {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.cervinschi.marin.javafx.briscola to javafx.fxml;
    exports com.cervinschi.marin.javafx.briscola;
    exports com.cervinschi.marin.javafx.briscola.controllers;
    exports com.cervinschi.marin.javafx.briscola.models;
    opens com.cervinschi.marin.javafx.briscola.controllers to javafx.fxml;
}