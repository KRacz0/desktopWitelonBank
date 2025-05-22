package com.kracz0.desktopwitelonbank.Controllers.Client.Modals;

import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TwoFactorController {

    public Label titleLabel;
    @FXML
        private Label emailLabel;

        @FXML
        private TextField codeField;

        @FXML
        private Button verifyButton;

        @FXML
        private Label errorLabel;

        private String email;

        private Stage loginStage;

        public void setEmail(String email) {
            this.email = email;
            emailLabel.setText("Kod wysłany na: " + email);
        }

        @FXML
        private void initialize() {
            errorLabel.setText("");
            verifyButton.setOnAction(event -> verifyCode());
        }

    private void verifyCode() {
        String code = codeField.getText().trim();
        if (code.isEmpty()) {
            errorLabel.setText("Wprowadź kod weryfikacyjny.");
            return;
        }

        AuthService authService = new AuthService();
        authService.verifyTwoFactorCode(email, code, response -> {
            Platform.runLater(() -> {
                if (response.isSuccess()) {
                    Model.getInstance().setLoggedUser(response.getUser());
                    Model.getInstance().getViewFactory().showClientWindow();

                    Stage thisStage = (Stage) errorLabel.getScene().getWindow();
                    thisStage.close();

                    if (loginStage != null) {
                        loginStage.close();
                    }


                    errorLabel.getScene().getWindow().hide();
                } else {
                    errorLabel.setText(response.getMessage());
                }
            });
        });
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

}
