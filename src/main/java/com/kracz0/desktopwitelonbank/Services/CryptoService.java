package com.kracz0.desktopwitelonbank.Services;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.CryptoWallet;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CryptoService {
    private Map<String, Double> cachedPrices = new HashMap<>();

    public Map<String, Double> getCryptoPrices() {
        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.KRYPTOWALUTY_CENY).GET().build();
            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return cachedPrices;
            }

            JSONObject json = new JSONObject(response.body());

            if (json.has("message")) {
                return cachedPrices;
            }

            Map<String, Double> prices = new HashMap<>();
            for (String key : json.keySet()) {
                try {
                    prices.put(key, json.getDouble(key));
                } catch (JSONException e) {
                }
            }

            cachedPrices = prices;
            return prices;

        } catch (Exception e) {
            e.printStackTrace();
            return cachedPrices;
        }
    }

    public Map<String, Double> getCachedPrices() {
        return cachedPrices != null ? cachedPrices : new HashMap<>();
    }

    public CryptoWallet getWallet() {
        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.PORTFEL).GET().build();
            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return new CryptoWallet();
            }

            JSONObject json = new JSONObject(response.body());

            if (!json.has("data")) {
                return new CryptoWallet();
            }

            JSONObject data = json.getJSONObject("data");

            if (!data.has("saldo_bitcoin") || !data.has("saldo_ethereum")) {
                return new CryptoWallet();
            }

            CryptoWallet wallet = new CryptoWallet();
            Field btcField = CryptoWallet.class.getDeclaredField("saldo_bitcoin");
            Field ethField = CryptoWallet.class.getDeclaredField("saldo_ethereum");

            btcField.setAccessible(true);
            ethField.setAccessible(true);

            btcField.set(wallet, Double.parseDouble(data.getString("saldo_bitcoin")));
            ethField.set(wallet, Double.parseDouble(data.getString("saldo_ethereum")));

            return wallet;

        } catch (Exception e) {
            e.printStackTrace();
            return new CryptoWallet();
        }
    }

    public String buyCrypto(int accountId, String symbol, double kwotaPLN) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("id_konta_pln", accountId);
            payload.put("symbol_krypto", symbol);
            payload.put("kwota_pln", kwotaPLN);

            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.INWESTYCJE_KUP)
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                return json.getString("message");
            } else {
                JSONObject error = new JSONObject(response.body());
                return error.optString("message", "Nieznany błąd.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Błąd połączenia z API.";
        }
    }

    public String sellCrypto(int accountId, String symbol, double iloscKrypto) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("id_konta_pln", accountId);
            payload.put("symbol_krypto", symbol);
            payload.put("ilosc_krypto", iloscKrypto);

            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.INWESTYCJE_SPRZEDAJ)
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                return json.getString("message");
            } else {
                JSONObject error = new JSONObject(response.body());
                return error.optString("message", "Nieznany błąd.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Błąd połączenia z API.";
        }
    }

}
