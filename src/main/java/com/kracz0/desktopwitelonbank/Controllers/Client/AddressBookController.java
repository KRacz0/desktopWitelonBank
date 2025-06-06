package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.Recipient;
import com.kracz0.desktopwitelonbank.Services.AddressBookService;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddressBookController implements Initializable {

    @FXML public Button add_Recipient_btn;
    @FXML private Button delete_btn;
    @FXML private ListView<Recipient> recipientsList;
    @FXML private StackPane modal_overlay;
    @FXML private TextField nazwaZdefiniowanaField;
    @FXML private TextField numerKontaField;
    @FXML private TextField rzeczywistaNazwaField;
    @FXML private TextField adres1Field;
    @FXML private TextField adres2Field;
    @FXML private VBox notificationBox;
    @FXML private Label notificationLabel;
    @FXML private StackPane notificationPane;


    private final AddressBookService addressBookService = new AddressBookService();
    private Recipient selectedRecipient = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        add_Recipient_btn.setOnAction(e -> modal_overlay.setVisible(true));

        recipientsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Recipient recipient, boolean empty) {
                super.updateItem(recipient, empty);

                if (empty || recipient == null) {
                    setGraphic(null);
                } else {
                    VBox card = createRecipientCard(recipient);
                    setGraphic(card);
                }
            }
        });

        loadRecipients();
    }

    private void loadRecipients() {
        new Thread(() -> {
            try {
                List<Recipient> recipients = addressBookService.getAllRecipients();
                Platform.runLater(() -> {
                    recipientsList.getItems().clear();
                    recipientsList.getItems().addAll(recipients);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showNotification( "Błąd podczas pobierania odbiorców."));
            }
        }).start();
    }

    @FXML
    private void submitRecipient() {
        String nazwa = nazwaZdefiniowanaField.getText();
        String konto = numerKontaField.getText();
        String rzeczywista = rzeczywistaNazwaField.getText();
        String adres1 = adres1Field.getText();
        String adres2 = adres2Field.getText();

        if (nazwa.isBlank() || konto.isBlank() || rzeczywista.isBlank()) {
            Platform.runLater(() -> showNotification( "Wypełnij wymagane pola."));
            return;
        }

        Recipient r = new Recipient();
        r.setNazwa_zdefiniowana(nazwa);
        r.setNr_konta(konto);
        r.setRzeczywista_nazwa(rzeczywista);
        r.setAdres_linia1(adres1);
        r.setAdres_linia2(adres2);

        if (selectedRecipient != null) {
            r.setId(selectedRecipient.getId());
            new Thread(() -> {
                try {
                    boolean success = addressBookService.updateRecipient(r);
                    Platform.runLater(() -> {
                        if (success) {
                            Platform.runLater(() -> showNotification( "Zaktualizowano odbiorcę."));
                            closeModal();
                            loadRecipients();
                        } else {
                            Platform.runLater(() -> showNotification( "Nie udało się zaktualizować odbiorcy."));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showNotification( "Błąd podczas edycji odbiorcy."));
                }
            }).start();
        } else {
            new Thread(() -> {
                try {
                    boolean success = addressBookService.addRecipient(r);
                    Platform.runLater(() -> {
                        if (success) {
                            Platform.runLater(() -> showNotification( "Odbiorca został zapisany."));
                            closeModal();
                            loadRecipients();
                        } else {
                            Platform.runLater(() -> showNotification("Nie udało się dodać odbiorcy."));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showNotification( "Błąd podczas dodawania odbiorcy."));
                }
            }).start();
        }
    }

    @FXML
    private void deleteRecipient() {
        if (selectedRecipient == null) return;

        new Thread(() -> {
            try {
                boolean success = addressBookService.deleteRecipient(selectedRecipient.getId());
                Platform.runLater(() -> {
                    if (success) {
                        showNotification("Odbiorca został usunięty.");
                        closeModal();
                        loadRecipients();
                    } else {
                        showNotification("Nie udało się usunąć odbiorcy.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showNotification("Błąd podczas usuwania odbiorcy."));
            }
        }).start();
    }

    private VBox createRecipientCard(Recipient recipient) {
        Label label = new Label(recipient.getNazwa_zdefiniowana() + " - " + recipient.getNr_konta());
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label name = new Label("Nazwa: " + recipient.getRzeczywista_nazwa());
        Label address = new Label("Adres: " + recipient.getAdres_linia1() + ", " + recipient.getAdres_linia2());

        VBox box = new VBox(label, name, address);
        box.setStyle("-fx-background-color: #f2f2f2; -fx-padding: 10; -fx-background-radius: 5;");
        box.setSpacing(5);

        box.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openEditModal(recipient);
            }
        });

        return box;
    }

    private void openEditModal(Recipient recipient) {
        selectedRecipient = recipient;

        nazwaZdefiniowanaField.setText(recipient.getNazwa_zdefiniowana());
        numerKontaField.setText(recipient.getNr_konta());
        rzeczywistaNazwaField.setText(recipient.getRzeczywista_nazwa());
        adres1Field.setText(recipient.getAdres_linia1());
        adres2Field.setText(recipient.getAdres_linia2());

        delete_btn.setVisible(true);
        modal_overlay.setVisible(true);
    }

    @FXML
    private void closeModal() {
        modal_overlay.setVisible(false);
        selectedRecipient = null;

        delete_btn.setVisible(false);
        nazwaZdefiniowanaField.clear();
        numerKontaField.clear();
        rzeczywistaNazwaField.clear();
        adres1Field.clear();
        adres2Field.clear();
    }

    private void showNotification(String message) {
        notificationLabel.setText(message);
        notificationBox.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(ev -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notificationBox);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(evt -> notificationBox.setVisible(false));
                fadeOut.play();
            });
            pause.play();
        });
        fadeIn.play();
    }
}
