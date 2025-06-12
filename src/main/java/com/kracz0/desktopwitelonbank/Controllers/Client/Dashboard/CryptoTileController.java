package com.kracz0.desktopwitelonbank.Controllers.Client.Dashboard;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CryptoTileController {

    @FXML private FontAwesomeIconView icon_crypto_lbl;
    @FXML private Label crypto_name_lbl;
    @FXML private Label crypto_price_lbl;
    @FXML private Label crypto_change_lbl;
    @FXML private Label crypto_amount_lbl;
    @FXML private Label crypto_value_lbl;

    public void setData(String nameWithSymbol, double amount, double price) {
        crypto_name_lbl.setText(nameWithSymbol);
        crypto_price_lbl.setText(String.format("%.2f PLN", price));
        crypto_amount_lbl.setText(String.format("%.8f %s", amount, nameWithSymbol.contains("BTC") ? "BTC" : "ETH"));
        crypto_value_lbl.setText(String.format("%.2f PLN", amount * price));

        icon_crypto_lbl.setGlyphSize(24);
        crypto_change_lbl.setText("");
    }

}
