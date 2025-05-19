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

    public List<Transfer> getPrzelewy(int kontoId, String typ) throws Exception {
        String url = ApiConfig.BASE_URL + "/konta/" + kontoId + "/przelewy?typ=" + typ;

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

                String typtransakcji = item.has("typ_transakcji") ? item.getString("typ_transakcji") : "brak typu";

                przelewy.add(new Transfer(
                        item.getString("tytul"),
                        item.getDouble("kwota"),
                        typtransakcji,
                        item.isNull("data_realizacji") ? "N/A" : item.getString("data_realizacji")
                ));

            }

            return przelewy;

        } else {
            throw new Exception("Błąd pobierania przelewów: " + body);
        }
    }
}

