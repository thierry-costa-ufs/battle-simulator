module com.game.battlesimulator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.game.battlesimulator to javafx.fxml;
    opens com.game.battlesimulator.controller to javafx.fxml;

    exports com.game.battlesimulator;
}