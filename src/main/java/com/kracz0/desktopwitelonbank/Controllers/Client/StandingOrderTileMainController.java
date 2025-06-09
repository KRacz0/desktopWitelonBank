package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.DTO.StandingOrder;
import com.kracz0.desktopwitelonbank.Services.StandingOrderDetailsService;
import com.kracz0.desktopwitelonbank.Services.StandingOrderMainService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class StandingOrderTileMainController {

    @FXML private Label nazwaOdbiorcyLabel;
    @FXML private Label kwotaLabel;
    @FXML private Label dataLabel;
    private StandingOrder order;
    private StandingOrderMainController parentController;

    public void setData(StandingOrder order, StandingOrderMainController parentController) {
        this.order = order;
        this.parentController = parentController;

        nazwaOdbiorcyLabel.setText(order.getNazwa_odbiorcy());
        kwotaLabel.setText(String.format("%.2f PLN", order.getKwota()));
        dataLabel.setText("Następne wykonanie: " + order.getData_nastepnego_wykonania());
    }

    @FXML
    private void handleDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2 && order != null) {
            StandingOrderDetailsService service = new StandingOrderDetailsService();
            StandingOrder fullOrder = service.getZlecenieStaleById(order.getId());
            if (fullOrder != null) {
                parentController.showDetailsModal(fullOrder);
            }
        }
    }

    @FXML
    private void handleEdit() {
        parentController.showEditModal(order);
    }

    @FXML
    private void handleDelete() {
        boolean success = new StandingOrderMainService().deleteStandingOrder(order.getId());
        if (success) {
            parentController.refreshOrders();
        }
    }
}

