package com.kracz0.desktopwitelonbank.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.Account;
import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class DashboardService {

    public List<Account> getKonta() throws Exception {
        HttpRequest request = ApiClient.authorizedRequest(ApiConfig.BASE_URL + "/konta")
                .GET().build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONArray arr = new JSONArray(response.body());
            List<Account> konta = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                konta.add(new Account(
                        json.getInt("id"),
                        json.getString("nr_konta"),
                        json.getDouble("saldo"),
                        json.has("waluta") ? json.getString("waluta") : "PLN"
                ));
            }
            return konta;
        } else {
            throw new Exception("Błąd pobierania kont: " + response.body());
        }
    }

    public List<Transfer> getPrzelewy(int kontoId) throws Exception {
        String url = ApiConfig.BASE_URL + "/konta/" + kontoId + "/przelewy";

        HttpRequest request = ApiClient.authorizedRequest(url).GET().build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        System.out.println("Odpowiedź z API (przelewy): " + body);

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(body);

            if (!json.has("data") || !json.get("data").toString().startsWith("[")) {
                throw new Exception("Brak listy 'data' lub niewłaściwy format: " + body);
            }

            JSONArray arr = json.getJSONArray("data");
            List<Transfer> przelewy = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);

                String typ = item.getString("typ");
                String data = item.isNull("data_realizacji") ? item.getString("data_zlecenia") : item.getString("data_realizacji");
                String waluta = item.optString("waluta_przelewu", "PLN");

                String tytul = item.getString("tytul");
                double kwota = item.getDouble("kwota");

                String nrKontaOdbiorcy = item.optString("nr_konta_odbiorcy", "brak");
                String nazwaOdbiorcy = item.optString("nazwa_odbiorcy", "brak");
                String nrKontaNadawcy = item.optString("nr_konta_nadawcy", "brak");

                String nazwaNadawcy = item.optString("nazwa_nadawcy", "brak");

                Transfer transfer = new Transfer(
                        tytul, kwota, typ, data, waluta,
                        nazwaOdbiorcy, nrKontaOdbiorcy,
                        nazwaNadawcy, nrKontaNadawcy
                );

                przelewy.add(transfer);
            }

            return przelewy;
        } else {
            throw new Exception("Błąd pobierania przelewów: " + body);
        }
    }
}

