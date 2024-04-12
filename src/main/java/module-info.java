module be.helha.applicine {
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
    requires java.sql;
    requires java.sql.rowset;
    requires okhttp3;
    requires org.json;
    requires java.desktop;
    requires annotations;
    requires spring.security.crypto;

    exports be.helha.applicine.controllers;
    opens be.helha.applicine.controllers to javafx.fxml;
    exports be.helha.applicine.models;
    exports be.helha.applicine.views;
    opens be.helha.applicine.views to javafx.fxml;
    exports be.helha.applicine;
    opens be.helha.applicine to javafx.fxml;
    exports be.helha.applicine.controllers.managercontrollers;
    opens be.helha.applicine.controllers.managercontrollers to javafx.fxml;
    exports be.helha.applicine.views.managerviews;
    opens be.helha.applicine.views.managerviews to javafx.fxml;
}