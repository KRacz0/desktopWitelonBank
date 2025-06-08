package com.kracz0.desktopwitelonbank.Services.Admin;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.AccountAdmin;
import com.kracz0.desktopwitelonbank.Models.DTO.AdminStats;
import com.kracz0.desktopwitelonbank.Models.DTO.Transfer;
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
                return Collections.emptyList();
            }

            JSONObject responseObject = new JSONObject(response.body());
            JSONArray dataArray = responseObject.getJSONArray("data");

            List<AccountAdmin> list = new ArrayList<>();

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                JSONObject user = obj.getJSONObject("uzytkownik");

                int id = obj.getInt("id");
                String nrKonta = obj.getString("nr_konta");
                double saldo = obj.getDouble("saldo");
                String waluta = obj.getString("waluta");
                double limitPrzelewu = obj.getDouble("limit_przelewu");
                boolean zablokowane = obj.getBoolean("zablokowane");
                String utworzono = obj.isNull("created_at") ? "brak danych" : obj.getString("created_at");
                String imie = user.optString("imie", "brak");
                String nazwisko = user.optString("nazwisko", "brak");
                String email = user.optString("email", "brak");
                String telefon = user.optString("telefon", "brak");

                if (obj.isNull("created_at")) {
                    System.out.println(" ID: " + id + " nie ma daty utworzenia");
                }


                AccountAdmin konto = new AccountAdmin(id, nrKonta, saldo, waluta, limitPrzelewu, zablokowane, utworzono,
                        imie, nazwisko, email, telefon);
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
                return null;
            }

            JSONObject obj = new JSONObject(response.body());
            JSONObject user = obj.getJSONObject("uzytkownik");

            int id = obj.getInt("id");
            String nrKonta = obj.getString("nr_konta");
            double saldo = obj.getDouble("saldo");
            String waluta = obj.getString("waluta");
            double limitPrzelewu = obj.getDouble("limit_przelewu");
            boolean zablokowane = obj.getBoolean("zablokowane");
            String utworzono = obj.isNull("created_at") ? "brak danych" : obj.getString("created_at");
            String imie = user.optString("imie", "brak");
            String nazwisko = user.optString("nazwisko", "brak");
            String email = user.optString("email", "brak");
            String telefon = user.optString("telefon", "brak");

            if (obj.isNull("created_at")) {
            }


            return new AccountAdmin(id, nrKonta, saldo, waluta, limitPrzelewu, zablokowane, utworzono,
                    imie, nazwisko, email, telefon);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Transfer> getTransfersForAccount(int accountId) {
        try {
            String url = ApiConfig.ADMIN_PRZELEWY + "/" + accountId;

            HttpRequest request = ApiClient.authorizedRequest(url).GET().build();
            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return Collections.emptyList();
            }

            JSONObject responseObject = new JSONObject(response.body());
            JSONArray array = responseObject.getJSONArray("data");
            List<Transfer> result = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                String tytul = obj.optString("tytul", "brak");
                double kwota = obj.optDouble("kwota", 0.0);
                String typTransakcji = obj.optString("typ", "wychodzacy");
                String data = obj.optString("data_realizacji", "brak");
                String waluta = obj.optString("waluta_przelewu", "brak");

                String nazwaNadawcy = obj.optString("nazwa_nadawcy", "");
                String nrKontaNadawcy = obj.optString("nr_konta_nadawcy", "");
                String nazwaOdbiorcy = obj.optString("nazwa_odbiorcy", "");
                String nrKontaOdbiorcy = obj.optString("nr_konta_odbiorcy", "");

                Transfer transfer = new Transfer(
                        tytul, kwota, typTransakcji, data, waluta,
                        nazwaOdbiorcy, nrKontaOdbiorcy,
                        nazwaNadawcy, nrKontaNadawcy
                );

                result.add(transfer);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean blockAccount(int idKonta) {
        return sendPatchRequest(ApiConfig.ADMIN_KONTA + "/" + idKonta + "/block");
    }

    public boolean unblockAccount(int idKonta) {
        return sendPatchRequest(ApiConfig.ADMIN_KONTA + "/" + idKonta + "/unblock");
    }

    private boolean sendPatchRequest(String url) {
        try {
            HttpRequest request = ApiClient.authorizedRequest(url)
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("PATCH response: " + response.body());
            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTransferLimit(int idKonta, double newLimit) {
        try {
            JSONObject json = new JSONObject();
            json.put("limit_przelewu", newLimit);

            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ADMIN_KONTA + "/" + idKonta + "/limit")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json.toString()))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("PATCH (limit) response: " + response.body());
            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public AdminStats getAdminStats() {
        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ADMIN_STATYSTYKI).GET().build();
            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return null;
            }

            JSONObject json = new JSONObject(response.body());
            AdminStats stats = new AdminStats();

            // Użytkownicy
            JSONObject uzytkownicy = json.getJSONObject("uzytkownicy");
            stats.setCalkowitaLiczbaUzytkownikow(uzytkownicy.getInt("calkowita_liczba"));
            stats.setLiczbaZweryfikowanych(uzytkownicy.getInt("liczba_zweryfikowanych"));
            stats.setNowiUzytkownicyOstatnie30Dni(uzytkownicy.getInt("nowi_uzytkownicy_ostatnie_30_dni"));

            // Konta bankowe
            JSONObject konta = json.getJSONObject("konta_bankowe");
            stats.setCalkowitaLiczbaKont(konta.getInt("calkowita_liczba"));
            stats.setLiczbaZablokowanychKont(konta.getInt("liczba_zablokowanych_kont"));

            JSONArray salda = konta.getJSONArray("podsumowanie_sald_wg_waluty");
            if (!salda.isEmpty()) {
                JSONObject pln = salda.getJSONObject(0);
                stats.setSumaSaldoPLN(pln.getDouble("calkowita_kwota"));
                stats.setLiczbaKontPLN(pln.getInt("liczba_kont"));
                stats.setSredniaSaldoPLN(pln.getDouble("srednia_kwota"));
            }

            // Karty
            JSONObject karty = json.getJSONObject("karty_platnicze");
            stats.setCalkowitaLiczbaKart(karty.getInt("calkowita_liczba"));
            stats.setLiczbaZablokowanychKart(karty.getInt("liczba_zablokowanych_kart"));

            // Przelewy
            JSONObject przelewy = json.getJSONObject("przelewy");
            stats.setCalkowitaLiczbaPrzelewow(przelewy.getInt("calkowita_liczba"));
            stats.setPrzelewy24h(przelewy.getInt("przelewy_ostatnie_24_godziny"));
            stats.setPrzelewy7dni(przelewy.getInt("przelewy_ostatnie_7_dni"));
            stats.setPrzelewy30dni(przelewy.getInt("przelewy_ostatnie_30_dni"));

            JSONArray kwoty = przelewy.getJSONArray("podsumowanie_kwot_wg_waluty");
            if (!kwoty.isEmpty()) {
                JSONObject pln = kwoty.getJSONObject(0);
                stats.setSumaKwotPrzelewowPLN(pln.getDouble("calkowita_kwota"));
                stats.setLiczbaPrzelewowPLN(pln.getInt("liczba_przelewow"));
            }

            JSONArray wgStatusu = przelewy.getJSONArray("liczba_wg_statusu");
            for (int i = 0; i < wgStatusu.length(); i++) {
                JSONObject obj = wgStatusu.getJSONObject(i);
                if ("zrealizowany".equalsIgnoreCase(obj.getString("status"))) {
                    stats.setLiczbaZrealizowanych(obj.getInt("liczba"));
                    break;
                }
            }

            // Krypto
            JSONObject krypto = json.getJSONObject("portfele_kryptowalut");
            stats.setLiczbaPortfeli(krypto.getInt("calkowita_liczba_portfeli"));
            stats.setLiczbaUzytkownikowZPortfelami(krypto.getInt("liczba_uzytkownikow_z_portfelami"));
            stats.setSumaBTC(krypto.getDouble("calkowita_suma_bitcoin"));
            stats.setSumaETH(krypto.getDouble("calkowita_suma_ethereum"));

            return stats;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Transfer> getAllTransfers(String status, Integer idKontaNadawcy, int page, int perPage) {
        try {
            StringBuilder urlBuilder = new StringBuilder(ApiConfig.ADMIN_PRZELEWY);
            urlBuilder.append("?page=").append(page).append("&per_page=").append(perPage);
            if (status != null) urlBuilder.append("&status=").append(status);
            if (idKontaNadawcy != null) urlBuilder.append("&id_konta_nadawcy=").append(idKontaNadawcy);

            HttpRequest request = ApiClient.authorizedRequest(urlBuilder.toString()).GET().build();
            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return Collections.emptyList();

            JSONObject responseObject = new JSONObject(response.body());
            JSONArray array = responseObject.getJSONArray("data");
            List<Transfer> result = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Transfer transfer = new Transfer(
                        obj.getString("tytul"),
                        obj.getDouble("kwota"),
                        obj.getString("status"),
                        obj.optString("data_realizacji", "-"),
                        obj.getString("waluta_przelewu"),
                        obj.optString("nazwa_odbiorcy"),
                        obj.optString("nr_konta_odbiorcy"),
                        obj.optString("nazwa_nadawcy"),
                        obj.optString("nr_konta_nadawcy")
                );

                result.add(transfer);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



}

