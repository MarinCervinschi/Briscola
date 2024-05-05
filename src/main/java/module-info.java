module com.cervinschi.marin.javafx.briscola {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.cervinschi.marin.javafx.briscola to javafx.fxml;
    exports com.cervinschi.marin.javafx.briscola;
}