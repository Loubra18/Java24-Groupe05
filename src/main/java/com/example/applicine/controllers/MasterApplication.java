package com.example.applicine.controllers;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * It is the main class of the application.
 * It is responsible for starting the application and switching between windows.
 */
public class MasterApplication extends Application {
    /**
     * The current opened window of the application.
     */
    private Window currentWindow;
    /**
     * Setter for the current window.
     * @param currentWindow The window to set as the current window.
     */
    public void setCurrentWindow(Window currentWindow) {
        this.currentWindow = currentWindow;
        System.out.println("Current window set to: " + currentWindow);
    }
    /**
     * Start point of the application.
     */
    @Override
    public void start(Stage stage) throws IOException {
        LoginApplication loginApplication = new LoginApplication();
        loginApplication.start(stage);
        this.currentWindow = stage;
    }
    /**
     * Switch to the login window and close the currentWindow.
     */
    public void toLogin() throws IOException{
        currentWindow.hide();
        LoginApplication loginApplication = new LoginApplication();
        loginApplication.start(new Stage());
    }
    /**
     * Switch to the client window and close the currentWindow.
     */
    public void toClient() throws Exception {
        currentWindow.hide();
        ClientInterfaceApplication clientInterfaceApplication = new ClientInterfaceApplication();
        clientInterfaceApplication.start(new Stage());
    }
    /**
     * Switch to the manager window and close the currentWindow.
     */
    public void toAdmin() throws Exception {
        currentWindow.hide();
        ManagerApplication managerApplication = new ManagerApplication();
        managerApplication.start(new Stage());
    }
    public static void main(String[] args) {
        launch();
    }
}
