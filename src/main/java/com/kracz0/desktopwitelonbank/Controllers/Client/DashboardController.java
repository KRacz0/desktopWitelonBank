package com.kracz0.desktopwitelonbank.Controllers.Client;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.Account;
import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Models.User;
import com.kracz0.desktopwitelonbank.Services.DashboardService;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Label income_lbl;
    public Label balance_lbl;
    public VBox sidebar;
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
    public Label expense_lbl;
    public FontAwesomeIconView icon_transaction_lbl;
    public Label transaction_category_lbl;
    public Label transaction_amount_lbl;
    public Label transaction_date_lbl;
    public Button showMore_transaction_btn;
    public FontAwesomeIconView icon_crypto_lbl;
    public Label crypto_name_lbl;
    public Label crypto_price_lbl;
    public Label crypto_change_btc;
    public Label crypto_amount_lbl;
    public Label crypto_value_lbl;
    public Button showMore_crypto_btn;


    @FXML
    private AreaChart<String, Number> financeChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final DashboardService dashboardService = new DashboardService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User user = Model.getInstance().getLoggedUser();
        user_name_lbl.setText("Witaj, " + user.getImie());
        login_date_lbl.setText(java.time.LocalTime.now().withNano(0) + ", " + java.time.LocalDate.now());

        new Thread(() -> {
            try {
                List<Account> accounts = dashboardService.getKonta();
                if (!accounts.isEmpty()) {
                    Account account = accounts.get(0);  // Główne konto
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

    private void loadTransactions(int accountId) {
        new Thread(() -> {
            try {
                List<Transfer> income = dashboardService.getPrzelewy(accountId, "przychodzace");
                List<Transfer> expense = dashboardService.getPrzelewy(accountId, "wychodzace");

                double incomeSum = income.stream().mapToDouble(Transfer::getKwota).sum();
                double expenseSum = expense.stream().mapToDouble(Transfer::getKwota).sum();

                Platform.runLater(() -> {
                    income_lbl.setText(String.format("+ %.2f PLN", incomeSum));
                    expense_lbl.setText(String.format("- %.2f PLN", expenseSum));

                    if (!income.isEmpty()) {
                        Transfer t = income.get(0); // ostatnia przychodząca
                        transaction_category_lbl.setText("Wpływ: " + t.getTytul());
                        transaction_amount_lbl.setText(String.format("+ %.2f PLN", t.getKwota()));
                        transaction_date_lbl.setText(t.getData());
                        icon_transaction_lbl.setGlyphName("BANK");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String formatAccountNumber(String number) {
        return number.replaceAll("....(?!$)", "$0 ");
    }
}

