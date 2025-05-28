package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.DTO.CryptoWallet;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Services.CryptoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CryptoController implements Initializable {

    @FXML private Label btcPriceLabel, ethPriceLabel;
    @FXML private Label btcAmountLabel, ethAmountLabel;
    @FXML private Label btcPlnValueLabel, ethPlnValueLabel;
    @FXML private Button buyBtcBtn, buyEthBtn;

    private final CryptoService cryptoService = new CryptoService();
    private Map<String, Double> prices = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();

        buyBtcBtn.setOnAction(event -> buyCrypto("BTC"));
        buyEthBtn.setOnAction(event -> buyCrypto("ETH"));
    }

    public void refreshData() {
        new Thread(() -> {
            Map<String, Double> fetchedPrices = cryptoService.getCryptoPrices();
            if (fetchedPrices == null || fetchedPrices.isEmpty()) {
                System.out.println("Nie udało się pobrać cen kryptowalut.");
                return;
            }

            prices = fetchedPrices;

            Platform.runLater(() -> {
                btcPriceLabel.setText(prices.containsKey("BTC") ? String.format("%.2f PLN", prices.get("BTC")) : "Brak danych");
                ethPriceLabel.setText(prices.containsKey("ETH") ? String.format("%.2f PLN", prices.get("ETH")) : "Brak danych");

                loadWallet();
            });
        }).start();
    }

    private void loadWallet() {
        new Thread(() -> {
            CryptoWallet wallet = cryptoService.getWallet();
            if (wallet == null) {
                System.out.println("Portfel kryptowalut jest pusty lub nie został załadowany.");
                wallet = new CryptoWallet();
            }

            double btc = wallet.getSaldoBitcoin();
            double eth = wallet.getSaldoEthereum();
            double btcValue = btc * prices.getOrDefault("BTC", 0.0);
            double ethValue = eth * prices.getOrDefault("ETH", 0.0);

            Platform.runLater(() -> {
                btcAmountLabel.setText(String.format("%.8f BTC", btc));
                ethAmountLabel.setText(String.format("%.8f ETH", eth));
                btcPlnValueLabel.setText(String.format("%.2f PLN", btcValue));
                ethPlnValueLabel.setText(String.format("%.2f PLN", ethValue));
            });
        }).start();
    }

    private void buyCrypto(String symbol) {
        int accountId = Model.getInstance().getLoggedUser().getId();
        double kwota = 100;

        new Thread(() -> {
            String result = cryptoService.buyCrypto(accountId, symbol, kwota);

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Zakup kryptowaluty");
                alert.setContentText(result);
                alert.showAndWait();
                refreshData();
            });
        }).start();
    }
}


