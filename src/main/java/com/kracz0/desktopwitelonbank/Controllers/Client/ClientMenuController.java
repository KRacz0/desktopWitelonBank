package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    public Button sidebar_dashboard_btn;
    public Button sidebar_transactions_btn;
    public Button sidebar_addressBook_btn;
    public Button sidebar_crypto_btn;
    public Button sidebar_profile_btn;
    public Button sidebar_settings_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners() {
        sidebar_dashboard_btn.setOnAction(event -> onDashboard());
        sidebar_transactions_btn.setOnAction(event -> onTransactions());
        sidebar_addressBook_btn.setOnAction(event -> onAddressBook());
    }

    private void onDashboard() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Dashboard");
    }

    private void onTransactions() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Transactions");
    }

    private void onAddressBook() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("AddressBook");
    }
}
