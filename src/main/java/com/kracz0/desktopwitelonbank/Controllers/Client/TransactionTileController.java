package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TransactionTileController {

    @FXML private Label transaction_category;
    @FXML private Label transaction_amount;
    @FXML private Label transaction_date;
    @FXML private FontAwesomeIconView icon_transaction;

    public void setData(Transfer transfer) {
        transaction_category.setText(transfer.getTytul());

        String znak = transfer.getTypTransakcji().equals("przychodzacy") ? "+" : "-";
        transaction_amount.setText(String.format("%s %.2f PLN", znak, transfer.getKwota()));

        String data = transfer.getData();
        if (data != null && data.length() >= 10) {
            transaction_date.setText(data.substring(0, 10));
        } else {
            transaction_date.setText("brak");
        }

        if (transfer.getTypTransakcji().equals("przychodzacy")) {
            transaction_amount.getStyleClass().add("transaction_income");
            icon_transaction.setGlyphName("ARROW_UP");
            icon_transaction.getStyleClass().add("transaction_income");
        } else {
            transaction_amount.getStyleClass().add("transaction_expense");
            icon_transaction.setGlyphName("ARROW_DOWN");
            icon_transaction.getStyleClass().add("transaction_expense");
        }
    }

}
