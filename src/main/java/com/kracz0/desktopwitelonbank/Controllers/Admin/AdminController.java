package com.kracz0.desktopwitelonbank.Controllers.Admin;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.AccountAdmin;
import com.kracz0.desktopwitelonbank.Models.DTO.AdminStats;
import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Services.Admin.AdminService;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import com.kracz0.desktopwitelonbank.Views.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML public BorderPane adminRoot;
    @FXML public HBox statsContainer;
    @FXML public ComboBox statusFilterComboBox;
    @FXML private ListView<String> kontaListView;
    @FXML private StackPane reportModalOverlay;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button adminLogoutButton;
    @FXML private ListView<String> allTransfersListView;
    private final AdminService adminService = new AdminService();
    private Map<String, Integer> kontoIdByDisplay = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAccountList();
        loadAllTransfers();
        adminLogoutButton.setOnAction(event -> onLogout());

        statusFilterComboBox.setOnAction(event -> {
            String selectedStatus = (String) statusFilterComboBox.getValue();

            String statusParam = selectedStatus != null && !selectedStatus.equalsIgnoreCase("wszystkie")
                    ? selectedStatus.toLowerCase()
                    : null;

            loadFilteredTransfers(statusParam);
        });


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

        loadAdminStats();
    }

    private void showAccountDetails(int idKonta) {
        new Thread(() -> {
            AccountAdmin konto = adminService.getAccountDetails(idKonta);
            if (konto != null) {
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/AdminAccountDetails.fxml"));
                        AnchorPane view = loader.load();

                        AdminAccountDetailsController controller = loader.getController();
                        controller.setKonto(konto);

                        adminRoot.setCenter(view);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }).start();
    }

    private VBox createStatCard(String title, String mainValue, String tooltipContent) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat_title");

        Label valueLabel = new Label(mainValue);
        valueLabel.getStyleClass().add("stat_value");

        VBox box = new VBox(titleLabel, valueLabel);
        box.setSpacing(5);
        box.setPrefWidth(220);
        box.getStyleClass().add("stat_card");

        Tooltip tooltip = new Tooltip(tooltipContent);
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setHideDelay(Duration.millis(100));
        tooltip.setWrapText(true);
        tooltip.setMaxWidth(300);

        box.setOnMouseEntered(e -> {
            Bounds bounds = box.localToScreen(box.getBoundsInLocal());
            tooltip.show(box, bounds.getMinX() + 10, bounds.getMinY() + box.getHeight() + 5);
        });

        box.setOnMouseExited(e -> {
            tooltip.hide();
        });

        return box;
    }

    private void loadAdminStats() {
        new Thread(() -> {
            AdminStats stats = adminService.getAdminStats();
            if (stats != null) {
                Platform.runLater(() -> {
                    statsContainer.getChildren().clear();

                    statsContainer.getChildren().add(createStatCard(
                            "Użytkownicy",
                            String.valueOf(stats.getCalkowitaLiczbaUzytkownikow()),
                            String.format("Zweryfikowani: %d\nNowi w 30 dni: %d",
                                    stats.getLiczbaZweryfikowanych(),
                                    stats.getNowiUzytkownicyOstatnie30Dni())
                    ));

                    statsContainer.getChildren().add(createStatCard(
                            "Konta bankowe",
                            String.valueOf(stats.getCalkowitaLiczbaKont()),
                            String.format("Liczba kont PLN: %d\nŚrednie saldo: %.2f zł\nSuma: %.2f zł",
                                    stats.getLiczbaKontPLN(),
                                    stats.getSredniaSaldoPLN(),
                                    stats.getSumaSaldoPLN())
                    ));

                    statsContainer.getChildren().add(createStatCard(
                            "Karty płatnicze",
                            String.valueOf(stats.getCalkowitaLiczbaKart()),
                            String.format("Zablokowane: %d\nZbliżeniowe: %d\nInternetowe: %d",
                                    stats.getLiczbaZablokowanychKart(),
                                    stats.getLiczbaAktywneZblizeniowe(),
                                    stats.getLiczbaAktywneInternetowe())
                    ));

                    statsContainer.getChildren().add(createStatCard(
                            "Przelewy",
                            String.valueOf(stats.getCalkowitaLiczbaPrzelewow()),
                            String.format("24h: %d\n7 dni: %d\n30 dni: %d\nZrealizowane: %d\nPLN: %.2f zł (%d szt.)",
                                    stats.getPrzelewy24h(),
                                    stats.getPrzelewy7dni(),
                                    stats.getPrzelewy30dni(),
                                    stats.getLiczbaZrealizowanych(),
                                    stats.getSumaKwotPrzelewowPLN(),
                                    stats.getLiczbaPrzelewowPLN())
                    ));

                    statsContainer.getChildren().add(createStatCard(
                            "Kryptowaluty",
                            String.valueOf(stats.getLiczbaPortfeli()),
                            String.format("Użytkownicy z portfelem: %d\nBTC: %.4f\nETH: %.4f",
                                    stats.getLiczbaUzytkownikowZPortfelami(),
                                    stats.getSumaBTC(),
                                    stats.getSumaETH())
                    ));
                });
            }
        }).start();
    }

    @FXML
    private void openReportModal() {
        reportModalOverlay.setVisible(true);
    }

    @FXML
    private void closeReportModal() {
        reportModalOverlay.setVisible(false);
    }

    @FXML
    private void downloadReport() {
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (start == null || end == null || end.isBefore(start)) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Wybierz poprawny zakres dat.");
            alert.showAndWait();
            return;
        }

        String dateFrom = start.toString();
        String dateTo = end.toString();

        new Thread(() -> {
            try {
                String urlWithParams = ApiConfig.ADMIN_RAPORTY + "?data_od=" + dateFrom + "&data_do=" + dateTo;
                HttpRequest request = ApiClient.authorizedRequest(urlWithParams).GET().build();
                HttpResponse<byte[]> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200) {
                    byte[] fileBytes = response.body();

                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Zapisz raport");
                    fileChooser.setInitialFileName("raport_przelewy_" + dateFrom + "_do_" + dateTo + ".pdf");
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

                    Platform.runLater(() -> {
                        File file = fileChooser.showSaveDialog(reportModalOverlay.getScene().getWindow());
                        if (file != null) {
                            try (FileOutputStream fos = new FileOutputStream(file)) {
                                fos.write(fileBytes);
                                closeReportModal();
                            } catch (IOException e) {
                                showError("Błąd zapisu pliku.");
                            }
                        }
                    });
                } else {
                    showError("Nie udało się pobrać raportu. Kod: " + response.statusCode());
                }

            } catch (Exception e) {
                e.printStackTrace();
                showError("Wystąpił błąd podczas pobierania raportu.");
            }
        }).start();
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message);
            alert.showAndWait();
        });
    }

    private void onLogout() {
        new Thread(() -> {
            try {
                HttpRequest request = ApiClient.authorizedRequest(ApiConfig.LOGOUT)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> {
                    Model.getInstance().logout();
                    ViewFactory viewFactory = Model.getInstance().getViewFactory();
                    viewFactory.showLoginWindow();

                    Stage stage = (Stage) adminLogoutButton.getScene().getWindow();
                    stage.close();
                });
            }
        }).start();
    }

    private void loadAllTransfers() {
        new Thread(() -> {
            List<Transfer> transfers = adminService.getAllTransfers(null, null, 1, 30); // domyślnie strona 1, 30 rekordów

            Platform.runLater(() -> {
                allTransfersListView.getItems().clear();
                for (Transfer t : transfers) {
                    String display = String.format("[%s] %s (%s) | %.2f %s | %s",
                            t.getTypTransakcji().toUpperCase(),
                            t.getNazwaDrugiejStrony(),
                            t.getNrKontaDrugiejStrony(),
                            t.getKwota(),
                            t.getWaluta(),
                            t.getTytul());
                    allTransfersListView.getItems().add(display);
                }
            });
        }).start();
    }

    private void loadFilteredTransfers(String status) {
        new Thread(() -> {
            List<Transfer> transfers = adminService.getAllTransfers(status, null, 1, 30);

            Platform.runLater(() -> {
                allTransfersListView.getItems().clear();
                for (Transfer t : transfers) {
                    String display = String.format("[%s] %s (%s) | %.2f %s | %s",
                            t.getTypTransakcji().toUpperCase(),
                            t.getNazwaDrugiejStrony(),
                            t.getNrKontaDrugiejStrony(),
                            t.getKwota(),
                            t.getWaluta(),
                            t.getTytul());
                    allTransfersListView.getItems().add(display);
                }
            });
        }).start();
    }

}


