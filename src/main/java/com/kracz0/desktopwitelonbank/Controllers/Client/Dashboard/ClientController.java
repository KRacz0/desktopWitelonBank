package com.kracz0.desktopwitelonbank.Controllers.Client.Dashboard;

import com.kracz0.desktopwitelonbank.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public BorderPane client_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Transactions" -> client_parent.setCenter(Model.getInstance().getViewFactory().getTransactionsView());
                case "AddressBook" -> client_parent.setCenter(Model.getInstance().getViewFactory().getAddressBookView());
                case "CryptoWallet" -> client_parent.setCenter(Model.getInstance().getViewFactory().getCryptoWalletView());
                case "ClientProfile" -> client_parent.setCenter(Model.getInstance().getViewFactory().getClientProfileView());
                case "StandingOrder" -> client_parent.setCenter(Model.getInstance().getViewFactory().getStandingOrderView());
                default -> client_parent.setCenter(Model.getInstance().getViewFactory().getDashboardView());
            }
        });
    }
}
