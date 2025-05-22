package com.kracz0.desktopwitelonbank.Services;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TransactionsService {

    public HttpResponse<String> sendTransfer(JSONObject json) {
        if (!validateTransferData(json)) {
            System.out.println(" Walidacja danych przelewu nie powiodła się.");
            return null;
        }

        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.PRZELEWY)
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Odpowiedź z API: " + response.statusCode() + " → " + response.body());
            return response;

        } catch (Exception e) {
            System.out.println(" Błąd wysyłania przelewu:");
            e.printStackTrace();
            return null;
        }
    }

    private boolean validateTransferData(JSONObject json) {
        return json.has("id_konta_nadawcy") &&
                json.has("nr_konta_odbiorcy") &&
                json.has("nazwa_odbiorcy") &&
                json.has("adres_odbiorcy_linia1") &&
                json.has("adres_odbiorcy_linia2") &&
                json.has("tytul") &&
                json.has("kwota") &&
                json.has("waluta_przelewu") &&
                json.getDouble("kwota") > 0 &&
                !json.getString("nr_konta_odbiorcy").isBlank() &&
                !json.getString("nazwa_odbiorcy").isBlank();
    }
}

