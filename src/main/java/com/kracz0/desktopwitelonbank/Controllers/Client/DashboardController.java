package com.kracz0.desktopwitelonbank.Controllers.Client;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
