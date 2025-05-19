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
                        json.getString("waluta")
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

        if (response.statusCode() == 200) {
            JSONArray arr = new JSONArray(response.body());
            List<Transfer> przelewy = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                przelewy.add(new Transfer(
                        json.getString("tytul"),
                        json.getDouble("kwota"),
                        json.getString("typ_transakcji"),
                        json.getString("data_realizacji")
                ));
            }
            return przelewy;
        } else {
            throw new Exception("Błąd pobierania przelewów: " + response.body());
        }
    }
}

