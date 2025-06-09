package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.DTO.Account;
import com.kracz0.desktopwitelonbank.Models.DTO.StandingOrder;
import com.kracz0.desktopwitelonbank.Models.Recipient;
import com.kracz0.desktopwitelonbank.Services.AddressBookService;
import com.kracz0.desktopwitelonbank.Services.DashboardService;
import com.kracz0.desktopwitelonbank.Services.StandingOrderMainService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StandingOrderMainController implements Initializable {

    @FXML private VBox ordersContainer;

    @FXML private StackPane modal_overlay;
    @FXML private Label modal_nazwaOdbiorcy;
    @FXML private Label modal_numerKonta;
    @FXML private Label modal_kwota;
    @FXML private Label modal_tytul;
    @FXML private Label modal_czestotliwosc;
    @FXML private Label modal_dataStartu;
    @FXML private Label modal_dataNastepna;
    @FXML private Label modal_dataZakonczenia;

    @FXML private StackPane modal_edit_overlay;
    @FXML private TextField edit_nazwa_odbiorcy;
    @FXML private TextField edit_nr_konta;
    @FXML private TextField edit_tytul;
    @FXML private TextField edit_kwota;
    @FXML private TextField edit_czestotliwosc;
    @FXML private DatePicker edit_data_startu;
    @FXML private DatePicker edit_data_zakonczenia;

    @FXML private StackPane modal_create_overlay;
    @FXML private TextField create_nazwa_odbiorcy;
    @FXML private TextField create_nr_konta;
    @FXML private TextField create_tytul;
    @FXML private TextField create_kwota;
    @FXML public ComboBox create_czestotliwosc_combo;
    @FXML private DatePicker create_data_startu;
    @FXML private DatePicker create_data_zakonczenia;

    @FXML private ListView<Recipient> address_recipient_list_standing;


    private StandingOrder orderBeingEdited;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStandingOrders();
    }

    private void loadStandingOrders() {
        List<StandingOrder> orders = new StandingOrderMainService()
                .getZleceniaStale().stream()
                .filter(StandingOrder::isAktywne)
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            ordersContainer.getChildren().clear();

            if (orders.isEmpty()) {
                Label noData = new Label("Brak zleceń stałych");
                noData.getStyleClass().add("no_data_label");
                ordersContainer.getChildren().add(noData);
                return;
            }

            for (StandingOrder order : orders) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/Partials/StandingOrderTileMain.fxml"));
                    HBox tile = loader.load();
                    StandingOrderTileMainController controller = loader.getController();
                    controller.setData(order, this);
                    ordersContainer.getChildren().add(tile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showDetailsModal(StandingOrder order) {
        modal_nazwaOdbiorcy.setText("Odbiorca: " + order.getNazwa_odbiorcy());
        modal_numerKonta.setText("Numer konta: " + order.getNr_konta_docelowego());
        modal_kwota.setText("Kwota: " + order.getKwota() + " PLN");
        modal_tytul.setText("Tytuł przelewu: " + order.getTytul_przelewu());
        modal_czestotliwosc.setText("Częstotliwość: " + order.getCzestotliwosc());
        modal_dataStartu.setText("Data startu: " + order.getData_startu());
        modal_dataNastepna.setText("Następne wykonanie: " + order.getData_nastepnego_wykonania());
        modal_dataZakonczenia.setText("Data zakończenia: " + order.getData_zakonczenia());

        modal_overlay.setVisible(true);
    }

    public void showEditModal(StandingOrder order) {
        this.orderBeingEdited = order;

        edit_nazwa_odbiorcy.setText(order.getNazwa_odbiorcy());
        edit_nr_konta.setText(order.getNr_konta_docelowego());
        edit_tytul.setText(order.getTytul_przelewu());
        edit_kwota.setText(String.valueOf(order.getKwota()));
        edit_czestotliwosc.setText(order.getCzestotliwosc());

        edit_data_startu.setValue(LocalDate.parse(order.getData_startu()));
        edit_data_zakonczenia.setValue(LocalDate.parse(order.getData_zakonczenia()));

        modal_edit_overlay.setVisible(true);
    }


    @FXML
    private void closeEditModal() {
        modal_edit_overlay.setVisible(false);
        orderBeingEdited = null;
    }

    @FXML
    public void refreshOrders() {
        loadStandingOrders();
    }

    @FXML
    private void saveEditedOrder() {
        if (orderBeingEdited == null) return;

        orderBeingEdited.setNazwa_odbiorcy(edit_nazwa_odbiorcy.getText());
        orderBeingEdited.setNr_konta_docelowego(edit_nr_konta.getText());
        orderBeingEdited.setTytul_przelewu(edit_tytul.getText());
        orderBeingEdited.setKwota(Double.parseDouble(edit_kwota.getText()));
        orderBeingEdited.setCzestotliwosc(edit_czestotliwosc.getText());
        orderBeingEdited.setData_startu(edit_data_startu.getValue().toString());
        orderBeingEdited.setData_zakonczenia(edit_data_zakonczenia.getValue().toString());

        boolean success = new StandingOrderMainService().updateStandingOrder(orderBeingEdited.getId(), orderBeingEdited);
        if (success) {
            closeEditModal();
            refreshOrders();
        }
    }

    @FXML
    private void openCreateModal() {
        clearCreateForm();
        modal_create_overlay.setVisible(true);
    }

    @FXML
    private void closeCreateModal() {
        modal_create_overlay.setVisible(false);
    }

    @FXML
    private void saveNewOrder() {
        try {
            StandingOrder newOrder = new StandingOrder();
            newOrder.setNazwa_odbiorcy(create_nazwa_odbiorcy.getText());
            newOrder.setNr_konta_docelowego(create_nr_konta.getText());
            newOrder.setTytul_przelewu(create_tytul.getText());
            newOrder.setKwota(Double.parseDouble(create_kwota.getText()));
            newOrder.setCzestotliwosc((String) create_czestotliwosc_combo.getValue());
            newOrder.setData_startu(create_data_startu.getValue().toString());
            newOrder.setData_zakonczenia(create_data_zakonczenia.getValue().toString());
            newOrder.setAktywne(true);

            List<Account> konta = new DashboardService().getKonta();
            if (konta.isEmpty()) {
                throw new Exception("Brak konta źródłowego dla zlecenia.");
            }

            newOrder.setId_konta_zrodlowego(konta.get(0).getId());

            boolean success = new StandingOrderMainService().createStandingOrder(newOrder);
            if (success) {
                closeCreateModal();
                refreshOrders();
            } else {
                System.err.println("Nie udało się utworzyć zlecenia.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openStandingOrderAddressBookPicker() {
        address_recipient_list_standing.setVisible(true);

        new Thread(() -> {
            try {
                List<Recipient> recipients = new AddressBookService().getAllRecipients();
                Platform.runLater(() -> {
                    address_recipient_list_standing.getItems().clear();
                    address_recipient_list_standing.getItems().addAll(recipients);

                    address_recipient_list_standing.setCellFactory(list -> new ListCell<>() {
                        @Override
                        protected void updateItem(Recipient item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getNazwa_zdefiniowana() + " - " + item.getNr_konta());
                        }
                    });

                    address_recipient_list_standing.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            fillCreateFormFromRecipient(address_recipient_list_standing.getSelectionModel().getSelectedItem());
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fillCreateFormFromRecipient(Recipient recipient) {
        if (recipient != null) {
            create_nazwa_odbiorcy.setText(recipient.getRzeczywista_nazwa());
            create_nr_konta.setText(recipient.getNr_konta());
            address_recipient_list_standing.setVisible(false);
        }
    }

    private void clearCreateForm() {
        create_nazwa_odbiorcy.clear();
        create_nr_konta.clear();
        create_tytul.clear();
        create_kwota.clear();
        create_data_startu.setValue(null);
        create_data_zakonczenia.setValue(null);
    }

    @FXML
    private void closeDetailsModal() {
        modal_overlay.setVisible(false);
    }
}


