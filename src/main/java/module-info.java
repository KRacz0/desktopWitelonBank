module com.kracz0.desktopwitelonbank {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires de.jensd.fx.glyphs.fontawesome;

    // Otwierasz pakiety kontrolerów dla FXML, aby FXML mógł tworzyć ich instancje
    opens com.kracz0.desktopwitelonbank.Controllers to javafx.fxml;
    opens com.kracz0.desktopwitelonbank.Controllers.Client to javafx.fxml;


    // Eksporty (jeśli używasz klas poza modułem)
    exports com.kracz0.desktopwitelonbank;
    exports com.kracz0.desktopwitelonbank.Controllers;
    exports com.kracz0.desktopwitelonbank.Controllers.Client;
    exports com.kracz0.desktopwitelonbank.Models;
    exports com.kracz0.desktopwitelonbank.Views;
}
