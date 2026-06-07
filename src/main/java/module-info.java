module com.game.battlesimulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    exports com.game.battlesimulator.view;

    opens com.game.battlesimulator.controller to javafx.fxml;
    opens com.game.battlesimulator.view to javafx.fxml;
}