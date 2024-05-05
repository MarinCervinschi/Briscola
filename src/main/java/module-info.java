module com.cervinschi.marin.javafx.briscola {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.cervinschi.marin.javafx.briscola to javafx.fxml;
    exports com.cervinschi.marin.javafx.briscola;
    exports com.cervinschi.marin.javafx.briscola.controllers;
    opens com.cervinschi.marin.javafx.briscola.controllers to javafx.fxml;
}