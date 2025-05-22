package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Services.DashboardService;
import com.kracz0.desktopwitelonbank.Services.TransactionsService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {
    public ListView<HBox> transactions_listview;
    public DatePicker startDatePicker;
    public DatePicker endDatePicker;
    public Button generateReportBtn;
    @FXML public Label nazwa_title_lbl;
    @FXML public Label konto_title_lbl;
    @FXML public AnchorPane new_transfer_modal;
    @FXML private StackPane modal_overlay;
    @FXML private Label typ_lbl;
    @FXML private Label konto_lbl;
    @FXML private Label nazwa_lbl;
    @FXML private Label tytul_lbl;
    @FXML private Label kwota_lbl;
    @FXML private Label data_lbl;

    @FXML private StackPane new_transfer_overlay;
    @FXML private TextField recipient_account_field;
    @FXML private TextField recipient_name_field;
    @FXML private TextField address_line1_field;
    @FXML private TextField address_line2_field;
    @FXML private TextField title_field;
    @FXML private TextField amount_field;
    @FXML private Button newTransferBtn;

    @FXML private VBox transfer_form;
    @FXML private VBox transfer_status_box;
    @FXML private ProgressIndicator loading_indicator;
    @FXML private Label transfer_status_message;
    @FXML private Button transfer_status_close_btn;

    @FXML private FontAwesomeIconView status_icon;
    @FXML private Label transfer_status_details;


    private final TransactionsService transactionsService = new TransactionsService();
    private final DashboardService dashboardService = new DashboardService();
    private List<Transfer> allTransfers = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAllTransfers();
        generateReportBtn.setOnAction(event -> filterAndDisplayTransfers());
        newTransferBtn.setOnAction(event -> openNewTransferModal());
        generateReportBtn.setOnAction(event -> filterAndDisplayTransfers());

        AnimationTimer modalOpener = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (Model.getInstance().shouldOpenTransferModal()) {
                    openNewTransferModal();
                    Model.getInstance().setOpenTransferModalFlag(false);
                    stop();
                }
            }
        };
        modalOpener.start();
    }

    @FXML
    public void closeModal() {
        modal_overlay.setVisible(false);
    }

    @FXML
    public void openNewTransferModal() {
        clearTransferForm();
        transfer_form.setVisible(true);
        transfer_status_box.setVisible(false);
        loading_indicator.setVisible(false);
        transfer_status_message.setText("");
        transfer_status_close_btn.setVisible(false);

        new_transfer_overlay.setVisible(true);
    }

    @FXML
    public void closeNewTransferModal() {
        new_transfer_overlay.setVisible(false);
    }

    private void loadAllTransfers() {
        new Thread(() -> {
            try {
                int accountId = Model.getInstance().getLoggedUser().getId();
                allTransfers = dashboardService.getPrzelewy(accountId);

                Platform.runLater(() -> showTransfers(allTransfers));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void filterAndDisplayTransfers() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        List<Transfer> filtered = allTransfers.stream()
                .filter(t -> {
                    try {
                        LocalDate date = LocalDate.parse(t.getData().substring(0, 10));
                        boolean afterStart = (start == null || !date.isBefore(start));
                        boolean beforeEnd = (end == null || !date.isAfter(end));
                        return afterStart && beforeEnd;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .sorted(Comparator.comparing(Transfer::getData).reversed())
                .toList();

        showTransfers(filtered);
    }

    private void showTransfers(List<Transfer> transfers) {
        transactions_listview.getItems().clear();

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

                transactions_listview.getItems().add(tile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        typ_lbl.setText(t.getTypTransakcji());
        konto_lbl.setText(t.getNrKontaDrugiejStrony());
        nazwa_lbl.setText(t.getNazwaDrugiejStrony());
        tytul_lbl.setText(t.getTytul());
        kwota_lbl.setText(String.format("%.2f %s", t.getKwota(), t.getWaluta()));
        data_lbl.setText(t.getData());

        modal_overlay.setVisible(true);

        System.out.println("Nazwa drugiej strony: " + t.getNazwaDrugiejStrony());
        System.out.println("Nr konta drugiej strony: " + t.getNrKontaDrugiejStrony());

    }

    @FXML
    public void sendTransfer() {

        transfer_form.setVisible(false);
        transfer_status_box.setVisible(true);
        loading_indicator.setVisible(true);
        transfer_status_close_btn.setVisible(false);
        transfer_status_message.setText("Wysyłanie przelewu...");

        String account = recipient_account_field.getText();
        String name = recipient_name_field.getText();
        String address1 = address_line1_field.getText();
        String address2 = address_line2_field.getText();
        String title = title_field.getText();
        String currency = "PLN";

        double amount;
        try {
            String rawAmount = amount_field.getText().replace(",", ".").trim();
            amount = Double.parseDouble(rawAmount);
        } catch (NumberFormatException e) {
            showTransferResult(false, "Niepoprawna kwota.", " ");
            return;
        }


        int accountId = Model.getInstance().getLoggedUser().getId();

        JSONObject json = new JSONObject();
        json.put("id_konta_nadawcy", accountId);
        json.put("nr_konta_odbiorcy", account);
        json.put("nazwa_odbiorcy", name);
        json.put("adres_odbiorcy_linia1", address1);
        json.put("adres_odbiorcy_linia2", address2);
        json.put("tytul", title);
        json.put("kwota", amount);
        json.put("waluta_przelewu", currency);

        new Thread(() -> {
            HttpResponse<String> response = transactionsService.sendTransfer(json);

            Platform.runLater(() -> {
                if (response != null && response.statusCode() == 201) {
                    showTransferResult(true, "Przelew wysłany pomyślnie", "");
                    loadAllTransfers();
                } else {
                    String reason = "Nieznany błąd.";
                    if (response != null && response.body() != null && !response.body().isBlank()) {
                        try {
                            JSONObject obj = new JSONObject(response.body());
                            reason = obj.optString("message", reason);
                        } catch (Exception e) {
                            reason = "Nie udało się odczytać szczegółów błędu.";
                        }
                    }
                    showTransferResult(false, "Błąd podczas wysyłania przelewu", reason);
                }
            });
        }).start();

    }

    private void clearTransferForm() {
        recipient_account_field.clear();
        recipient_name_field.clear();
        address_line1_field.clear();
        address_line2_field.clear();
        title_field.clear();
        amount_field.clear();
    }

    private void showTransferResult(boolean success, String message, String details) {
        loading_indicator.setVisible(false);
        transfer_status_message.setText(message);
        transfer_status_close_btn.setVisible(true);

        if (success) {
            status_icon.setGlyphName("CHECK_CIRCLE");
            status_icon.setGlyphSize(96);
            status_icon.getStyleClass().setAll("status_icon_success");
            transfer_status_details.setVisible(false);
        } else {
            status_icon.setGlyphName("TIMES_CIRCLE");
            status_icon.setGlyphSize(96);
            status_icon.getStyleClass().setAll("status_icon_error");

            transfer_status_details.setText(details);
            transfer_status_details.setVisible(true);
        }

        transfer_status_message.setStyle(success
                ? "-fx-text-fill: #2ecc71; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

}

