package com.kracz0.desktopwitelonbank.Controllers.Client.Dashboard;

import com.kracz0.desktopwitelonbank.Controllers.Client.StandingOrder.StandingOrderTileController;
import com.kracz0.desktopwitelonbank.Controllers.Client.Transactions.TransactionTileController;
import com.kracz0.desktopwitelonbank.Models.Card;
import com.kracz0.desktopwitelonbank.Models.DTO.Account;
import com.kracz0.desktopwitelonbank.Models.DTO.CryptoWallet;
import com.kracz0.desktopwitelonbank.Models.DTO.StandingOrder;
import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Models.User;
import com.kracz0.desktopwitelonbank.Services.CryptoService;
import com.kracz0.desktopwitelonbank.Services.DashboardService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {
    @FXML public Label balance_lbl;
    @FXML public Label user_name_lbl;
    @FXML public Label login_date_lbl;
    @FXML public Label card_type_1_lbl;
    @FXML public Label card_amount_1_lbl;
    @FXML public Label card_number_1_lbl;
    @FXML public Label card_type_2_lbl;
    @FXML public Label card_amount_2_lbl;
    @FXML public Label card_number_2_lbl;
    @FXML public Label card_expiry_2_lbl;
    @FXML public VBox standing_orders_box;
    @FXML public Button showMore_standingOrder_btn;
    @FXML private Label card_expiry_1_lbl;
    @FXML public Button new_transaction_btn;
    @FXML public Button buy_crypto_btn;
    @FXML public Label balance_period_lbl;
    @FXML public Label account_number_lbl;
    @FXML public Button showMore_transaction_btn;
    @FXML public Button showMore_crypto_btn;
    @FXML public VBox crypto_box;
    @FXML private VBox transactions_box;
    @FXML private Label income_lbl;
    @FXML private Label expense_lbl;

    private final DashboardService dashboardService = new DashboardService();
    private final CardsService cardsService = new CardsService();
    private final CryptoService cryptoService = new CryptoService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User user = Model.getInstance().getLoggedUser();
        user_name_lbl.setText(" Witaj, " + user.getImie());
        login_date_lbl.setText(java.time.LocalTime.now().withNano(0) + ", " + java.time.LocalDate.now());

        int kontoId = Model.getInstance().getLoggedUser().getId();
        loadCards(kontoId);
        loadCryptoTiles();
        loadStandingOrders();
        refreshDashboardData();

        new Thread(() -> {
            try {
                List<Account> accounts = dashboardService.getKonta();
                if (!accounts.isEmpty()) {
                    Account account = accounts.get(0);
                    int accountId = account.getId();

                    Platform.runLater(() -> {
                        account_number_lbl.setText(formatAccountNumber(account.getNrKonta()));
                        balance_lbl.setText(String.format("%.2f PLN", account.getSaldo()));
                    });

                    loadTransactions(accountId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new_transaction_btn.setOnAction(event -> {
            Model.getInstance().setOpenTransferModalFlag(true);
            Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Transactions");
        });

        buy_crypto_btn.setOnAction(event -> {
            Model.getInstance().setOpenTransferModalFlag(true);
            Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("CryptoWallet");
        });
    }

    private void loadTransactions(int accountId) {
        new Thread(() -> {
            try {
                List<Transfer> allTransfers = dashboardService.getPrzelewy(accountId);

                LocalDate today = LocalDate.now();
                LocalDate thresholdDate = today.minusDays(30);

                List<Transfer> last30Days = allTransfers.stream()
                        .filter(t -> {
                            String dateStr = t.getData();
                            if (dateStr == null || dateStr.length() < 10) return false;
                            try {
                                LocalDate date = LocalDate.parse(dateStr.substring(0, 10));
                                return !date.isBefore(thresholdDate);
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .toList();

                double incomeSum = last30Days.stream()
                        .filter(t -> t.getTypTransakcji().equals("przychodzacy"))
                        .mapToDouble(Transfer::getKwota)
                        .sum();

                double expenseSum = last30Days.stream()
                        .filter(t -> t.getTypTransakcji().equals("wychodzacy"))
                        .mapToDouble(Transfer::getKwota)
                        .sum();

                List<Transfer> latestForHistory = allTransfers.stream()
                        .sorted(Comparator.comparing(Transfer::getData).reversed())
                        .limit(3)
                        .toList();

                Platform.runLater(() -> {
                    income_lbl.setText(String.format("+ %.2f PLN", incomeSum));
                    expense_lbl.setText(String.format("- %.2f PLN", expenseSum));
                    renderLastTransactions(latestForHistory);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void loadStandingOrders() {
        new Thread(() -> {
            List<StandingOrder> orders = dashboardService.getZleceniaStale()
                    .stream()
                    .filter(StandingOrder::isAktywne)
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                standing_orders_box.getChildren().clear();

                if (orders.isEmpty()) {
                    Label noOrdersLabel = new Label("Brak zleceń stałych");
                    noOrdersLabel.getStyleClass().add("no_data_label");
                    standing_orders_box.getChildren().add(noOrdersLabel);
                    return;
                }

                for (StandingOrder order : orders) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/client/Partials/StandingOrderTile.fxml"));
                        HBox tile = loader.load();
                        StandingOrderTileController controller = loader.getController();
                        controller.setData(order);
                        standing_orders_box.getChildren().add(tile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        }).start();
    }

    private String formatAccountNumber(String number) {
        return number.replaceAll("....(?!$)", "$0 ");
    }

    private void renderLastTransactions(List<Transfer> transactions) {
        Platform.runLater(() -> {
            transactions_box.getChildren().clear();

            List<Transfer> last3 = transactions.stream().limit(3).toList();
            for (Transfer t : last3) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/Partials/TransactionTile.fxml"));
                    HBox tile = loader.load();

                    TransactionTileController controller = loader.getController();
                    controller.setData(t);

                    transactions_box.getChildren().add(tile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadCards(int kontoId) {
        new Thread(() -> {
            try {
                List<Card> cards = cardsService.getKarty(kontoId);

                Platform.runLater(() -> {
                    if (cards.size() >= 1) {
                        Card c1 = cards.get(0);
                        card_type_1_lbl.setText(c1.getTypKarty());
                        card_number_1_lbl.setText(c1.getNrKarty());
                        card_expiry_1_lbl.setText("Ważna do: " + c1.getDataWaznosci());
                        card_amount_1_lbl.setText(c1.isZablokowana() ? "Zablokowana" :
                                String.format("Limit: %.2f PLN", c1.getLimitDzienny()));
                    } else {
                        card_type_1_lbl.setText("Brak karty");
                        card_number_1_lbl.setText("Nie posiadasz karty");
                        card_expiry_1_lbl.setText("");
                        card_amount_1_lbl.setText("Zamów w naszym banku");
                    }

                    if (cards.size() >= 2) {
                        Card c2 = cards.get(1);
                        card_type_2_lbl.setText(c2.getTypKarty());
                        card_number_2_lbl.setText(c2.getNrKarty());
                        if (card_expiry_2_lbl != null)
                            card_expiry_2_lbl.setText("Ważna do: " + c2.getDataWaznosci());

                        card_amount_2_lbl.setText(c2.isZablokowana() ? "Zablokowana" :
                                String.format("Limit: %.2f PLN", c2.getLimitDzienny()));
                    } else {
                        card_type_2_lbl.setText("Brak karty");
                        card_number_2_lbl.setText("Nie posiadasz karty");
                        card_amount_2_lbl.setText("Zamów w naszym banku");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadCryptoTiles() {
        new Thread(() -> {
            Map<String, Double> prices = cryptoService.getCryptoPrices();
            CryptoWallet wallet = cryptoService.getWallet();

            if (prices == null || prices.isEmpty() || wallet == null) {
                System.out.println("Nie udało się załadować danych kryptowalut.");
                return;
            }

            Platform.runLater(() -> {
                crypto_box.getChildren().clear();

                try {
                    // BTC
                    FXMLLoader btcLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/Partials/CryptoTile.fxml"));
                    HBox btcTile = btcLoader.load();
                    CryptoTileController btcController = btcLoader.getController();
                    btcController.setData("Bitcoin (BTC)",  wallet.getSaldoBitcoin(), prices.getOrDefault("BTC", 0.0));
                    crypto_box.getChildren().add(btcTile);

                    // ETH
                    FXMLLoader ethLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/Partials/CryptoTile.fxml"));
                    HBox ethTile = ethLoader.load();
                    CryptoTileController ethController = ethLoader.getController();
                    ethController.setData("Ethereum (ETH)", wallet.getSaldoEthereum(), prices.getOrDefault("ETH", 0.0));
                    crypto_box.getChildren().add(ethTile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    public void refreshDashboardData() {
        User user = Model.getInstance().getLoggedUser();
        user_name_lbl.setText("Witaj, " + user.getImie());
        login_date_lbl.setText(java.time.LocalTime.now().withNano(0) + ", " + java.time.LocalDate.now());

        int kontoId = user.getId();
        loadCards(kontoId);
        loadCryptoTiles();

        new Thread(() -> {
            try {
                List<Account> accounts = dashboardService.getKonta();
                if (!accounts.isEmpty()) {
                    Account account = accounts.get(0);
                    int accountId = account.getId();

                    Platform.runLater(() -> {
                        account_number_lbl.setText(formatAccountNumber(account.getNrKonta()));
                        balance_lbl.setText(String.format("%.2f PLN", account.getSaldo()));
                    });

                    loadTransactions(accountId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void goToCrypto() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("CryptoWallet");
    }

    @FXML
    private void goToTransactions() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("Transactions");
    }

    @FXML
    private void goToStandingOrders() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set("StandingOrder");
    }
}

