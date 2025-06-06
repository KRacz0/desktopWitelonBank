package com.kracz0.desktopwitelonbank.Controllers.Client.Modals;

import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Models.User;
import com.kracz0.desktopwitelonbank.Services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class TwoFactorController {

    @FXML private Label emailLabel;
    @FXML private TextField digit0, digit1, digit2, digit3, digit4, digit5;
    @FXML private Button verifyButton;
    @FXML private Label errorLabel;

    private String email;
    private Stage loginStage;

    public void setEmail(String email) {
        this.email = email;
        emailLabel.setText("Kod wysłany na: " + email);
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    @FXML
    private void initialize() {
        errorLabel.setText("");

        setupInputBehavior(digit0, digit1, null, 0);
        setupInputBehavior(digit1, digit2, digit0, 1);
        setupInputBehavior(digit2, digit3, digit1, 2);
        setupInputBehavior(digit3, digit4, digit2, 3);
        setupInputBehavior(digit4, digit5, digit3, 4);
        setupInputBehavior(digit5, null, digit4, 5);

        verifyButton.setOnAction(event -> verifyCode());
    }

    private void setupInputBehavior(TextField current, TextField next, TextField previous, int index) {
        current.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 1) {
                current.setText(newVal.substring(0, 1));
                return;
            }

            if (!newVal.matches("\\d")) {
                current.clear();
                return;
            }

            if (next != null && newVal.length() == 1) {
                next.requestFocus();
            }
        });

        current.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && current.getText().isEmpty() && previous != null) {
                previous.requestFocus();
                previous.clear();
            }

            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    String paste = clipboard.getString().replaceAll("\\D", "");
                    if (paste.length() == 6) {
                        TextField[] digits = {digit0, digit1, digit2, digit3, digit4, digit5};
                        for (int i = 0; i < 6; i++) {
                            digits[i].setText(String.valueOf(paste.charAt(i)));
                        }
                        digit5.requestFocus();
                    }
                    event.consume();
                }
            }
        });

        current.setOnDragDetected(e -> e.consume());
    }

    private void verifyCode() {
        String code = (digit0.getText() + digit1.getText() + digit2.getText() +
                digit3.getText() + digit4.getText() + digit5.getText()).trim();

        if (!code.matches("\\d{6}")) {
            errorLabel.setText("Wprowadź pełny 6-cyfrowy kod.");
            return;
        }

        AuthService authService = new AuthService();
        authService.verifyTwoFactorCode(email, code, new AuthService.AuthCallback() {
            @Override
            public void onResult(AuthService.AuthResponse response) {
                Platform.runLater(() -> {
                    if (response.isSuccess()) {
                        Model.getInstance().setLoggedUser(response.getUser());
                        Model.getInstance().getViewFactory().showClientWindow();

                        Stage thisStage = (Stage) errorLabel.getScene().getWindow();
                        thisStage.close();
                        if (loginStage != null) loginStage.close();
                    } else {
                        errorLabel.setText(response.getMessage());
                    }
                });
            }
        });
    }
}
