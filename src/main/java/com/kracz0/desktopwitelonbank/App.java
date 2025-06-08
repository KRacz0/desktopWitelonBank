package com.kracz0.desktopwitelonbank;

import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Views.ViewFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Model.getInstance().getViewFactory().showLoginWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
