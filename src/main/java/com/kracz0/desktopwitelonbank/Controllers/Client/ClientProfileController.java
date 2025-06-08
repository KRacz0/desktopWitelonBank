package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.DTO.Account;
import com.kracz0.desktopwitelonbank.Models.DTO.CryptoWallet;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Models.User;
import com.kracz0.desktopwitelonbank.Services.CryptoService;
import com.kracz0.desktopwitelonbank.Services.DashboardService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ClientProfileController implements Initializable {

    @FXML private Label full_name_lbl;
    @FXML private Label email_lbl;
    @FXML private Label phone_lbl;
    @FXML private Label account_number_lbl;
    @FXML private Label account_balance_lbl;
    @FXML private Label crypto_balance_lbl;

    private final DashboardService dashboardService = new DashboardService();
    private final CryptoService cryptoService = new CryptoService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadProfileData();
    }

    private void loadProfileData() {
        new Thread(() -> {
            try {
                User user = Model.getInstance().getLoggedUser();
                List<Account> accounts = dashboardService.getKonta();
                Account mainAccount = accounts.isEmpty() ? null : accounts.get(0);

                CryptoWallet wallet = cryptoService.getWallet();
                Map<String, Double> prices = cryptoService.getCryptoPrices();
                double cryptoBalance = calculateCryptoValue(wallet, prices);

                Platform.runLater(() -> {
                    full_name_lbl.setText(user.getImie() + " " + user.getNazwisko());
                    email_lbl.setText(user.getEmail());
                    if (phone_lbl != null) phone_lbl.setText("brak danych");

                    if (mainAccount != null) {
                        account_number_lbl.setText(mainAccount.getNrKonta());
                        account_balance_lbl.setText(String.format("%.2f PLN", mainAccount.getSaldo()));
                    } else {
                        account_number_lbl.setText("brak");
                        account_balance_lbl.setText("brak");
                    }

                    crypto_balance_lbl.setText(String.format("%.2f PLN", cryptoBalance));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private double calculateCryptoValue(CryptoWallet wallet, Map<String, Double> prices) {
        double btc = wallet.getSaldoBitcoin();
        double eth = wallet.getSaldoEthereum();
        double btcPrice = prices.getOrDefault("BTC", 0.0);
        double ethPrice = prices.getOrDefault("ETH", 0.0);
        return btc * btcPrice + eth * ethPrice;
    }
}


