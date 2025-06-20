package com.kracz0.desktopwitelonbank.Controllers.Client.StandingOrder;

import com.kracz0.desktopwitelonbank.Models.DTO.StandingOrder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StandingOrderTileController {

    @FXML
    private Label order_recipient;
    @FXML private Label order_amount;
    @FXML private Label order_next_date;

    public void setData(StandingOrder order) {
        order_recipient.setText(order.getNazwa_odbiorcy());
        order_amount.setText(String.format("%.2f PLN", order.getKwota()));

        String date = order.getData_nastepnego_wykonania();
        order_next_date.setText(date != null && date.length() >= 10 ? date.substring(0, 10) : "brak");
    }
}

