package com.kracz0.desktopwitelonbank.Controllers.Client.Transactions;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Models.Recipient;
import com.kracz0.desktopwitelonbank.Services.AddressBookService;
import com.kracz0.desktopwitelonbank.Services.DashboardService;
import com.kracz0.desktopwitelonbank.Services.TransactionsService;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
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
import javafx.stage.FileChooser;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {
    public ListView<HBox> transactions_listview;
    @FXML public Label nazwa_title_lbl;
    @FXML public Label konto_title_lbl;
    @FXML public AnchorPane new_transfer_modal;
    @FXML public Label exportStatusLabel;
    @FXML public VBox exportStatusBox;
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

    @FXML private StackPane exportModalOverlay;
    @FXML private DatePicker exportStartDate;
    @FXML private DatePicker exportEndDate;
    @FXML private ComboBox<String> exportTypeComboBox;

    @FXML private VBox address_book_picker;
    @FXML private ListView<Recipient> address_recipient_list;

    private final TransactionsService transactionsService = new TransactionsService();
    private final DashboardService dashboardService = new DashboardService();
    private List<Transfer> allTransfers = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAllTransfers();
        newTransferBtn.setOnAction(event -> openNewTransferModal());

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

    @FXML
    private void openExportModal() {
        exportModalOverlay.setVisible(true);
    }

    @FXML
    private void closeExportModal() {
        exportModalOverlay.setVisible(false);
    }

    @FXML
    private void exportPDF() {
        LocalDate start = exportStartDate.getValue();
        LocalDate end = exportEndDate.getValue();
        String typ = exportTypeComboBox.getValue();

        exportStatusBox.setVisible(true);

        if (start == null || end == null || end.isBefore(start)) {
            exportStatusLabel.setText("Niepoprawny zakres dat.");
            return;
        }

        exportStatusLabel.setText("Eksportuję dane...");

        int kontoId = Model.getInstance().getLoggedUser().getId();
        String fullQuery = "?data_od=" + start + "&data_do=" + end +
                ("wszystkie".equalsIgnoreCase(typ) ? "" : "&typ=" + typ);
        String url = ApiConfig.BASE_URL + "/konta/" + kontoId + "/przelewy/export" + fullQuery;

        new Thread(() -> {
            try {
                HttpRequest request = ApiClient.authorizedRequest(url).GET().build();
                HttpResponse<byte[]> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofByteArray());

                int statusCode = response.statusCode();
                String contentType = response.headers().firstValue("Content-Type").orElse("brak nagłówka");

                Platform.runLater(() -> {
                    if (statusCode == 200 &&
                            (contentType.contains("application/pdf") || contentType.contains("octet-stream"))) {

                        byte[] csvData = response.body();

                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Zapisz historię przelewów");
                        fileChooser.setInitialFileName("historia_przelewow.pdf");
                        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

                        File file = fileChooser.showSaveDialog(exportModalOverlay.getScene().getWindow());

                        if (file != null) {
                            try (FileOutputStream fos = new FileOutputStream(file)) {
                                fos.write(csvData);
                                exportStatusLabel.setText("Plik został zapisany pomyślnie.");
                            } catch (IOException e) {
                                exportStatusLabel.setText("Błąd zapisu pliku.");
                            }
                        } else {
                            exportStatusLabel.setText("Zapis pliku został anulowany.");
                        }
                    } else {
                        byte[] body = response.body();
                        String preview = new String(body, 0, Math.min(body.length, 300));
                        System.out.println("Treść odpowiedzi (preview):\n" + preview);

                        exportStatusLabel.setText("Nie udało się pobrać pliku.\nKod: " + statusCode);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> exportStatusLabel.setText("Błąd podczas eksportu CSV."));
            }
        }).start();
    }



    private void showAlert(Alert.AlertType type, String msg) {
        Platform.runLater(() -> new Alert(type, msg).showAndWait());
    }

    @FXML
    private void openAddressBookPicker() {
        address_book_picker.setVisible(true);
        transfer_form.setVisible(false);
        transfer_status_box.setVisible(false);

        // Wczytanie odbiorców z API
        new Thread(() -> {
            try {
                List<Recipient> recipients = new AddressBookService().getAllRecipients();
                Platform.runLater(() -> {
                    address_recipient_list.getItems().clear();
                    address_recipient_list.getItems().addAll(recipients);

                    address_recipient_list.setCellFactory(list -> new ListCell<>() {
                        @Override
                        protected void updateItem(Recipient item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                            } else {
                                setText(item.getNazwa_zdefiniowana() + " - " + item.getNr_konta());
                            }
                        }
                    });

                    address_recipient_list.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            fillTransferFormFromRecipient(address_recipient_list.getSelectionModel().getSelectedItem());
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fillTransferFormFromRecipient(Recipient r) {
        if (r == null) return;

        recipient_account_field.setText(r.getNr_konta());
        recipient_name_field.setText(r.getRzeczywista_nazwa());
        address_line1_field.setText(r.getAdres_linia1());
        address_line2_field.setText(r.getAdres_linia2());

        address_book_picker.setVisible(false);
        transfer_form.setVisible(true);
    }

    @FXML
    private void closeAddressBookPicker() {
        address_book_picker.setVisible(false);
        transfer_form.setVisible(true);
    }



}

