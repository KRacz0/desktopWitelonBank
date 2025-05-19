package com.kracz0.desktopwitelonbank.Views;

import com.kracz0.desktopwitelonbank.Controllers.Client.ClientMenuController;
import com.kracz0.desktopwitelonbank.Controllers.Client.Modals.TwoFactorController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {

    private final StringProperty clientSelectedMenuItem;
    private AnchorPane dashboardView;
    private AnchorPane transactionsView;
    private AnchorPane addressBookView;

    public ViewFactory() {
        this.clientSelectedMenuItem = new SimpleStringProperty("");
    }


    // Client Views //
    public StringProperty getClientSelectedMenuItem() {
        return clientSelectedMenuItem;
    }

    public AnchorPane getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/Client/Dashboard.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public AnchorPane getTransactionsView() {
        if (transactionsView == null) {
            try {
                transactionsView = new FXMLLoader(getClass().getResource("/Fxml/Client/Transactions.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transactionsView;
    }

    public AnchorPane getAddressBookView() {
        if (addressBookView == null) {
            try {
                addressBookView = new FXMLLoader(getClass().getResource("/Fxml/Client/AddressBook.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return addressBookView;
    }

    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    public void showClientWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Witelon Bank");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTwoFactorModal(String email, Stage loginStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Modals/TwoFactorModal.fxml"));
            Parent root = loader.load();

            TwoFactorController controller = loader.getController();
            controller.setEmail(email);
            controller.setLoginStage(loginStage); // ⬅️ przekazujemy Stage do zamknięcia

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Weryfikacja dwuetapowa");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createStage(FXMLLoader loader) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Witelon Bank");
        stage.show();
    }
}
