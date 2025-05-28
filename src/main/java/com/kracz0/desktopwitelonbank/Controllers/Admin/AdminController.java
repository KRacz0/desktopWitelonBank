package com.kracz0.desktopwitelonbank.Controllers.Admin;

import com.kracz0.desktopwitelonbank.Models.DTO.AccountAdmin;
import com.kracz0.desktopwitelonbank.Services.Admin.AdminService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private ListView<String> kontaListView;
    private final AdminService adminService = new AdminService();
    private Map<String, Integer> kontoIdByDisplay = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAccountList();
    }

    private void loadAccountList() {
        new Thread(() -> {
            List<AccountAdmin> konta = adminService.getAllAccounts();
            Platform.runLater(() -> {
                kontaListView.getItems().clear();
                kontoIdByDisplay.clear();

                for (AccountAdmin konto : konta) {
                    String display = String.format("%s - Saldo: %.2f %s",
                            konto.getNumerKonta(),
                            konto.getAktualneSaldo(),
                            konto.getWaluta()
                    );
                    kontaListView.getItems().add(display);
                    kontoIdByDisplay.put(display, konto.getId());
                }

                kontaListView.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        String selected = kontaListView.getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            int idKonta = kontoIdByDisplay.get(selected);
                            showAccountDetails(idKonta);
                        }
                    }
                });
            });
        }).start();
    }

    private void showAccountDetails(int idKonta) {
        new Thread(() -> {
            AccountAdmin konto = adminService.getAccountDetails(idKonta);
            if (konto != null) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Szczegóły konta");
                    alert.setHeaderText("Konto: " + konto.getNumerKonta());
                    alert.setContentText(
                            "Saldo: " + konto.getAktualneSaldo() + " " + konto.getWaluta() + "\n" +
                                    "Limit przelewu: " + konto.getLimitPrzelewuDzienny() + "\n" +
                                    "Zablokowane: " + (konto.isCzyZablokowane() ? "Tak" : "Nie") + "\n" +
                                    "Utworzono: " + konto.getUtworzono()
                    );
                    alert.showAndWait();
                });
            }
        }).start();
    }
}


