package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Controllers.Client.Dashboard.DashboardController;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import com.kracz0.desktopwitelonbank.Views.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    public Button sidebar_dashboard_btn;
    public Button sidebar_transactions_btn;
    public Button sidebar_addressBook_btn;
    public Button sidebar_crypto_btn;
    public Button sidebar_profile_btn;
    public Button sidebar_logout_btn;
    public Button sidebar_standingOrder_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners() {
        sidebar_dashboard_btn.setOnAction(event -> onDashboard());
        sidebar_transactions_btn.setOnAction(event -> onTransactions());
        sidebar_addressBook_btn.setOnAction(event -> onAddressBook());
        sidebar_crypto_btn.setOnAction(event -> onCryptoWallet());
        sidebar_logout_btn.setOnAction(event -> onLogout());
        sidebar_profile_btn.setOnAction(event -> onClientProfile());
        sidebar_standingOrder_btn.setOnAction(event -> onStandingOrder());
    }

    private void onDashboard() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Dashboard");

        DashboardController controller = Model.getInstance().getViewFactory().getDashboardController();
        if (controller != null) {
            controller.refreshDashboardData();
        }
    }

    private void onTransactions() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Transactions");
    }

    private void onAddressBook() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("AddressBook");
    }

    private void onCryptoWallet() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("CryptoWallet");
    }

    private void onClientProfile() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("ClientProfile");
    }

    private void onStandingOrder() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("StandingOrder");
    }

    private void onLogout() {
        new Thread(() -> {
            try {
                HttpRequest request = ApiClient.authorizedRequest(ApiConfig.LOGOUT)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> {
                    Model.getInstance().logout();
                    ViewFactory viewFactory = Model.getInstance().getViewFactory();
                    viewFactory.showLoginWindow();

                    Stage stage = (Stage) sidebar_logout_btn.getScene().getWindow();
                    stage.close();
                });
            }
        }).start();
    }
}
