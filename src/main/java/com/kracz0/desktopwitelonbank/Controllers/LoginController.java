package com.kracz0.desktopwitelonbank.Controllers;

import com.kracz0.desktopwitelonbank.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public TextField login_fld;
    public TextField password_fld;
    public Button login_btn;
    public Label error_lbl;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event -> {
            Stage stage = (Stage) login_btn.getScene().getWindow();
            stage.close();

            Model.getInstance().getViewFactory().showClientWindow();
        });

    }
}
