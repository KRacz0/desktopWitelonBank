package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.DTO.CryptoWallet;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Services.CryptoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CryptoController implements Initializable {

    @FXML public ToggleButton modePlnToggle;
    @FXML public ToggleButton modeCryptoToggle;
    @FXML public AnchorPane crypto_modal_box;
    @FXML private Label crypto_conversion_label;
    @FXML private Label btcPriceLabel, ethPriceLabel;
    @FXML private Label btcAmountLabel, ethAmountLabel;
    @FXML private Label btcPlnValueLabel, ethPlnValueLabel;
    @FXML private Button buyBtcBtn, buyEthBtn;
    @FXML private Button sellBtcBtn, sellEthBtn;
    @FXML private StackPane crypto_modal_overlay;
    @FXML private Label crypto_modal_title, crypto_symbol_label, crypto_status_label;
    @FXML private TextField crypto_input_field;
    private boolean manualInputIsPln = true;



    private final CryptoService cryptoService = new CryptoService();
    private Map<String, Double> prices = new HashMap<>();
    private String selectedOperation = "";
    private String selectedSymbol = "";
    private ToggleGroup inputModeGroup = new ToggleGroup();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshData();

        buyBtcBtn.setOnAction(event -> openCryptoModal("BTC", "BUY"));
        buyEthBtn.setOnAction(event -> openCryptoModal("ETH", "BUY"));
        sellBtcBtn.setOnAction(event -> openCryptoModal("BTC", "SELL"));
        sellEthBtn.setOnAction(event -> openCryptoModal("ETH", "SELL"));
        modePlnToggle.setToggleGroup(inputModeGroup);
        modeCryptoToggle.setToggleGroup(inputModeGroup);

    }

    public void refreshData() {
        new Thread(() -> {
            Map<String, Double> fetchedPrices = cryptoService.getCryptoPrices();
            prices = fetchedPrices != null && !fetchedPrices.isEmpty()
                    ? fetchedPrices
                    : cryptoService.getCachedPrices();

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

    @FXML
    private void confirmCryptoOperation() {
        String input = crypto_input_field.getText().replace(",", ".").trim();
        if (input.isEmpty()) {
            crypto_status_label.setText("Wprowadź poprawną wartość.");
            return;
        }

        new Thread(() -> {
            int accountId = Model.getInstance().getLoggedUser().getId();
            String result = "";

            try {
                if ("BUY".equals(selectedOperation)) {
                    double kwotaPln = manualInputIsPln
                            ? Double.parseDouble(input)
                            : Double.parseDouble(input) * prices.getOrDefault(selectedSymbol, 0.0);
                    result = cryptoService.buyCrypto(accountId, selectedSymbol, kwotaPln);
                } else if ("SELL".equals(selectedOperation)) {
                    double iloscKrypto = manualInputIsPln
                            ? Double.parseDouble(input) / prices.getOrDefault(selectedSymbol, 0.0)
                            : Double.parseDouble(input);
                    result = cryptoService.sellCrypto(accountId, selectedSymbol, iloscKrypto);
                }
            } catch (NumberFormatException e) {
                Platform.runLater(() -> crypto_status_label.setText("Niepoprawna liczba."));
                return;
            }

            String finalResult = result;
            Platform.runLater(() -> {
                crypto_status_label.setText(finalResult);
                refreshData();
            });
        }).start();
    }

    private void openCryptoModal(String symbol, String operationType) {
        this.selectedSymbol = symbol;
        this.selectedOperation = operationType;

        crypto_modal_title.setText(operationType.equals("BUY") ? "Kup kryptowalutę" : "Sprzedaj kryptowalutę");
        crypto_symbol_label.setText("Wybrano: " + symbol);
        crypto_input_field.setPromptText(operationType.equals("BUY") ? "Kwota w PLN" : "Ilość " + symbol);
        crypto_modal_overlay.setVisible(true);
        crypto_input_field.clear();
        crypto_status_label.setText("");
        crypto_conversion_label.setText("");

        crypto_input_field.textProperty().addListener((obs, oldVal, newVal) -> updateConversionLabel(newVal));

        manualInputIsPln = true;
        modePlnToggle.setSelected(true);

        inputModeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == modePlnToggle) {
                manualInputIsPln = true;
            } else {
                manualInputIsPln = false;
            }
            updatePromptText();
            updateConversionLabel(crypto_input_field.getText());
        });

    }

    private void updateConversionLabel(String input) {
        input = input.replace(",", ".").trim();
        if (input.isEmpty() || selectedSymbol.isEmpty() || selectedOperation.isEmpty()) {
            crypto_conversion_label.setText("");
            return;
        }

        try {
            double value = Double.parseDouble(input);
            double price = prices.getOrDefault(selectedSymbol, 0.0);

            if (price == 0.0) {
                crypto_conversion_label.setText("Brak danych o kursie.");
                return;
            }

            if (manualInputIsPln) {
                double cryptoAmount = value / price;
                crypto_conversion_label.setText(String.format("Otrzymasz: %.8f %s", cryptoAmount, selectedSymbol));
            } else {
                double plnAmount = value * price;
                crypto_conversion_label.setText(String.format("Otrzymasz: %.2f PLN", plnAmount));
            }


        } catch (NumberFormatException e) {
            crypto_conversion_label.setText("Niepoprawna wartość.");
        }
    }

    private void updatePromptText() {
        if (manualInputIsPln) {
            crypto_input_field.setPromptText("Kwota w PLN");
        } else {
            crypto_input_field.setPromptText("Ilość " + selectedSymbol);
        }
    }

    @FXML
    private void closeCryptoModal() {
        crypto_modal_overlay.setVisible(false);
        crypto_input_field.clear();
        crypto_status_label.setText("");
    }

}


