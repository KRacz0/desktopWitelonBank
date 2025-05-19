package com.kracz0.desktopwitelonbank.Controllers;

import com.kracz0.desktopwitelonbank.Models.Model;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.kracz0.desktopwitelonbank.Services.AuthService.*;

public class LoginController implements Initializable {
    public TextField login_fld;
    public PasswordField password_fld;
    public Button login_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event -> {
            String email = login_fld.getText().trim();
            String haslo = password_fld.getText().trim();

            if (email.isEmpty() || haslo.isEmpty()) {
                error_lbl.setText("Wypełnij oba pola.");
                return;
            }

            error_lbl.setText("Logowanie...");

            // Logika logowania w osobnym wątku
            new Thread(() -> {
                try {
                    String verifiedEmail = login(email, haslo);

                    // Po zalogowaniu (kod 200), otwórz modal do 2FA
                    Platform.runLater(() -> showTwoFactorModal(verifiedEmail));

                } catch (Exception e) {
                    Platform.runLater(() -> error_lbl.setText(e.getMessage()));
                }
            }).start();
        });
    }

    private void showTwoFactorModal(String email) {
        Stage loginStage = (Stage) login_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().showTwoFactorModal(email, loginStage);
    }


}

