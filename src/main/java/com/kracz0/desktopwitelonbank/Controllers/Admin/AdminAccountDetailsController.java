package com.kracz0.desktopwitelonbank.Controllers.Admin;

import com.kracz0.desktopwitelonbank.Controllers.Client.TransactionTileController;
import com.kracz0.desktopwitelonbank.Models.DTO.AccountAdmin;
import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import com.kracz0.desktopwitelonbank.Services.Admin.AdminService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.List;

public class AdminAccountDetailsController {

    @FXML private Label kontoLabel;
    @FXML private Label saldoLabel;
    @FXML private Label walutaLabel;
    @FXML private Label limitLabel;
    @FXML private Label statusLabel;
    @FXML private Label utworzonoLabel;
    @FXML private StackPane modal_overlay;
    @FXML private Label typ_lbl;
    @FXML private Label konto_title_lbl;
    @FXML private Label konto_lbl;
    @FXML private Label nazwa_title_lbl;
    @FXML private Label nazwa_lbl;
    @FXML private Label tytul_lbl;
    @FXML private Label kwota_lbl;
    @FXML private Label data_lbl;
    @FXML private Label imieNazwiskoLabel;
    @FXML private Label emailLabel;
    @FXML private Label telefonLabel;
    @FXML private Button toggleBlockButton;
    @FXML private StackPane limit_modal_overlay;
    @FXML private TextField limitField;
    @FXML private Button backButton;

    private AccountAdmin currentKonto;

    public void setKonto(AccountAdmin konto) {
        this.currentKonto = konto;

        kontoLabel.setText("Numer konta: " + konto.getNumerKonta());
        saldoLabel.setText("Saldo: " + konto.getAktualneSaldo());
        walutaLabel.setText("Waluta: " + konto.getWaluta());
        limitLabel.setText("Limit przelewu: " + konto.getLimitPrzelewuDzienny());
        statusLabel.setText("Zablokowane: " + (konto.isCzyZablokowane() ? "Tak" : "Nie"));
        utworzonoLabel.setText("Utworzono: " + konto.getUtworzono());
        imieNazwiskoLabel.setText("Imię i nazwisko: " + konto.getImie() + " " + konto.getNazwisko());
        emailLabel.setText("Email: " + konto.getEmail());
        telefonLabel.setText("Telefon: " + konto.getTelefon());

        updateBlockButtonText();
        loadTransfers(konto.getId());
    }

    @FXML private ListView<HBox> transactionsListView;
    private final AdminService adminService = new AdminService();

    private void loadTransfers(int accountId) {
        new Thread(() -> {
            List<Transfer> transfers = adminService.getTransfersForAccount(accountId);

            Platform.runLater(() -> {
                transactionsListView.getItems().clear();

                for (Transfer transfer : transfers) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Partials/TransactionTile.fxml"));
                        HBox tile = loader.load();

                        TransactionTileController controller = loader.getController();
                        controller.setData(transfer);

                        tile.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                openTransactionModal(transfer);
                            }
                        });

                        transactionsListView.getItems().add(tile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }).start();
    }

    private void openTransactionModal(Transfer t) {
        String typ = t.getTypTransakcji();

        if (typ.equals("przychodzacy")) {
            nazwa_title_lbl.setText("Nazwa nadawcy:");
            konto_title_lbl.setText("Numer konta nadawcy:");
        } else {
            nazwa_title_lbl.setText("Nazwa odbiorcy:");
            konto_title_lbl.setText("Numer konta odbiorcy:");
        }

        typ_lbl.setText(typ);
        konto_lbl.setText(t.getNrKontaDrugiejStrony());
        nazwa_lbl.setText(t.getNazwaDrugiejStrony());
        tytul_lbl.setText(t.getTytul());
        kwota_lbl.setText(String.format("%.2f %s", t.getKwota(), t.getWaluta()));
        data_lbl.setText(t.getData());

        modal_overlay.setVisible(true);
    }

    private void updateBlockButtonText() {
        if (currentKonto.isCzyZablokowane()) {
            toggleBlockButton.setText("Odblokuj konto");
        } else {
            toggleBlockButton.setText("Zablokuj konto");
        }
    }

    @FXML
    private void openLimitModal() {
        limitField.setText(String.valueOf(currentKonto.getLimitPrzelewuDzienny()));
        limit_modal_overlay.setVisible(true);
    }
    @FXML
    private void closeModal() {
        modal_overlay.setVisible(false);
    }
    @FXML
    private void closeLimitModal() {
        limit_modal_overlay.setVisible(false);
    }

    @FXML
    private void handleToggleBlock() {
        new Thread(() -> {
            boolean success;
            if (currentKonto.isCzyZablokowane()) {
                success = adminService.unblockAccount(currentKonto.getId());
            } else {
                success = adminService.blockAccount(currentKonto.getId());
            }

            if (success) {
                // aktualizuj stan
                currentKonto.setCzyZablokowane(!currentKonto.isCzyZablokowane());
                Platform.runLater(() -> {
                    statusLabel.setText("Zablokowane: " + (currentKonto.isCzyZablokowane() ? "Tak" : "Nie"));
                    updateBlockButtonText();
                });
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText(null);
                    alert.setContentText("Nie udało się zmienić statusu konta.");
                    alert.showAndWait();
                });
            }
        }).start();
    }

    @FXML
    private void submitLimitChange() {
        try {
            double newLimit = Double.parseDouble(limitField.getText());
            new Thread(() -> {
                boolean success = adminService.updateTransferLimit(currentKonto.getId(), newLimit);
                if (success) {
                    Platform.runLater(() -> {
                        currentKonto.setLimitPrzelewuDzienny(newLimit);
                        limitLabel.setText("Limit przelewu: " + newLimit);
                        closeLimitModal();
                    });
                } else {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd");
                        alert.setContentText("Nie udało się zaktualizować limitu.");
                        alert.showAndWait();
                    });
                }
            }).start();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Błąd danych");
            alert.setHeaderText(null);
            alert.setContentText("Wprowadź poprawną wartość liczbową.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleBackToAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
            Parent root = loader.load();

            Scene scene = backButton.getScene();
            if (scene != null) {
                BorderPane rootPane = (BorderPane) scene.getRoot();
                rootPane.setCenter(root);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


