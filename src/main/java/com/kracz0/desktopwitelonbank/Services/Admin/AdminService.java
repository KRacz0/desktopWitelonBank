package com.kracz0.desktopwitelonbank.Services.Admin;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.AccountAdmin;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminService {

    public List<AccountAdmin> getAllAccounts() {
        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ADMIN_KONTA).GET().build();
            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Błąd pobierania kont admina: " + response.body());
                return Collections.emptyList();
            }

            JSONObject responseObject = new JSONObject(response.body());
            JSONArray dataArray = responseObject.getJSONArray("data");

            List<AccountAdmin> list = new ArrayList<>();

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);

                int id = obj.getInt("id");
                String nrKonta = obj.getString("nr_konta");
                double saldo = obj.getDouble("saldo");
                String waluta = obj.getString("waluta");
                double limitPrzelewu = obj.getDouble("limit_przelewu");
                boolean zablokowane = obj.getBoolean("zablokowane");
                String utworzono = obj.isNull("created_at") ? "brak danych" : obj.getString("created_at");

                if (obj.isNull("created_at")) {
                    System.out.println(" ID: " + id + " nie ma daty utworzenia");
                }


                AccountAdmin konto = new AccountAdmin(id, nrKonta, saldo, waluta, limitPrzelewu, zablokowane, utworzono);
                list.add(konto);
            }

            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public AccountAdmin getAccountDetails(int idKonta) {
        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ADMIN_KONTA + "/" + idKonta).GET().build();
            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Błąd pobierania szczegółów konta: " + response.body());
                return null;
            }

            JSONObject obj = new JSONObject(response.body());

            int id = obj.getInt("id");
            String nrKonta = obj.getString("nr_konta");
            double saldo = obj.getDouble("saldo");
            String waluta = obj.getString("waluta");
            double limitPrzelewu = obj.getDouble("limit_przelewu");
            boolean zablokowane = obj.getBoolean("zablokowane");
            String utworzono = obj.isNull("created_at") ? "brak danych" : obj.getString("created_at");

            if (obj.isNull("created_at")) {
                System.out.println("ID: " + id + " nie ma daty utworzenia");
            }


            return new AccountAdmin(id, nrKonta, saldo, waluta, limitPrzelewu, zablokowane, utworzono);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

