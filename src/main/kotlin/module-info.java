module by.varyvoda.matvey.potentialmethod {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires kotlin.stdlib.jdk8;
    requires java.desktop;

    opens by.varyvoda.matvey.potentialmethod to javafx.fxml;
    exports by.varyvoda.matvey.potentialmethod;
}