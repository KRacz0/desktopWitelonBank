package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Models.Card;
import com.kracz0.desktopwitelonbank.Models.DTO.Account;
import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Models.User;
import com.kracz0.desktopwitelonbank.Services.DashboardService;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Label balance_lbl;
    public Label user_name_lbl;
    public Label login_date_lbl;
    public Label card_type_1_lbl;
    public Label card_amount_1_lbl;
    public Label card_number_1_lbl;
    public Label card_type_2_lbl;
    public Label card_amount_2_lbl;
    public Label card_number_2_lbl;
    public Button new_card_btn;
    public Button new_transaction_btn;
    public Button buy_crypto_btn;
    public Label balance_period_lbl;
    public Label account_number_lbl;
    public Button showMore_transaction_btn;
    public FontAwesomeIconView icon_crypto_lbl;
    public Label crypto_name_lbl;
    public Label crypto_price_lbl;
    public Label crypto_change_btc;
    public Label crypto_amount_lbl;
    public Label crypto_value_lbl;
    public Button showMore_crypto_btn;
    @FXML private VBox transactions_box;
    @FXML private Label income_lbl;
    @FXML private Label expense_lbl;

    @FXML private AreaChart<String, Number> financeChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final DashboardService dashboardService = new DashboardService();
    private final CardsService cardsService = new CardsService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User user = Model.getInstance().getLoggedUser();
        user_name_lbl.setText("Witaj, " + user.getImie());
        login_date_lbl.setText(java.time.LocalTime.now().withNano(0) + ", " + java.time.LocalDate.now());

        int kontoId = Model.getInstance().getLoggedUser().getId();
        loadCards(kontoId);


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
    }

    private void loadTransactions(int accountId) {
        new Thread(() -> {
            try {
                List<Transfer> allTransfers = dashboardService.getPrzelewy(accountId);

                LocalDate today = LocalDate.now();
                LocalDate thresholdDate = today.minusDays(30);

                // Filtrowanie wpływów i wydatków z ostatnich 30 dni
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
                    renderLastTransactions(latestForHistory); // pełna historia
                });

                System.out.println("Income sum (last 30 days): " + incomeSum);
                System.out.println("Expense sum (last 30 days): " + expenseSum);

            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    if (!cards.isEmpty()) {
                        Card first = cards.get(0);
                        card_type_1_lbl.setText(first.getTypKarty());
                        card_number_1_lbl.setText(first.getNrKarty());
                        card_amount_1_lbl.setText(first.isZablokowana() ? "Zablokowana" :
                                String.format("Limit: %.2f PLN", first.getLimitDzienny()));
                    }
                    if (cards.size() > 1) {
                        Card second = cards.get(1);
                        card_type_2_lbl.setText(second.getTypKarty());
                        card_number_2_lbl.setText(second.getNrKarty());
                        card_amount_2_lbl.setText(second.isZablokowana() ? "Zablokowana" :
                                String.format("Limit: %.2f PLN", second.getLimitDzienny()));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}

