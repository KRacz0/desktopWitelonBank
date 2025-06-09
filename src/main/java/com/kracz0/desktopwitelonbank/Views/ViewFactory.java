package com.kracz0.desktopwitelonbank.Views;

import com.kracz0.desktopwitelonbank.Controllers.Admin.AdminAccountDetailsController;
import com.kracz0.desktopwitelonbank.Controllers.Client.CryptoController;
import com.kracz0.desktopwitelonbank.Controllers.Client.DashboardController;
import com.kracz0.desktopwitelonbank.Controllers.Client.Modals.TwoFactorController;
import com.kracz0.desktopwitelonbank.Models.DTO.AccountAdmin;
import com.kracz0.desktopwitelonbank.Models.Model;
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
    private AnchorPane cryptoWalletView;
    private AnchorPane adminAccountDetailsView;
    private AnchorPane clientProfileView;
    private AnchorPane standingOrderView;
    private DashboardController dashboardController;

    public ViewFactory() {
        this.clientSelectedMenuItem = new SimpleStringProperty("");
    }

    public StringProperty getClientSelectedMenuItem() {
        return clientSelectedMenuItem;
    }

    public AnchorPane getDashboardView() {
        if (dashboardView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Dashboard.fxml"));
                dashboardView = loader.load();
                dashboardController = loader.getController();
            } catch (IOException e) {
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

    public AnchorPane getCryptoWalletView() {
        if (cryptoWalletView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Crypto.fxml"));
                cryptoWalletView = loader.load();

                CryptoController controller = loader.getController();
                controller.refreshData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Crypto.fxml"));
                loader.load();
                CryptoController controller = loader.getController();
                controller.refreshData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return cryptoWalletView;
    }

    public AnchorPane getClientProfileView() {
        if (clientProfileView == null) {
            try {
                clientProfileView = new FXMLLoader(getClass().getResource("/Fxml/Client/ClientProfile.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return clientProfileView;
    }

    public AnchorPane getStandingOrderView() {
        if (standingOrderView == null) {
            try {
                standingOrderView = new FXMLLoader(getClass().getResource("/Fxml/Client/StandingOrderMain.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return standingOrderView;
    }


    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    public void showClientWindow() {
        try {
            FXMLLoader loader;
            if (Model.getInstance().getLoggedUser().isAdministrator()) {
                loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Client.fxml"));
            }

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Witelon Bank");
            stage.setResizable(false);
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
            controller.setLoginStage(loginStage);

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Weryfikacja dwuetapowa");
            modalStage.setResizable(false);
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
        stage.setResizable(false);
        stage.show();
    }

    public DashboardController getDashboardController() {
        return dashboardController;
    }

    public AnchorPane getAdminAccountDetailsView(AccountAdmin konto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/AdminAccountDetails.fxml"));
            adminAccountDetailsView = loader.load();

            AdminAccountDetailsController controller = loader.getController();
            controller.setKonto(konto);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return adminAccountDetailsView;
    }

}
