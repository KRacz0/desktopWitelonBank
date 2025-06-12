package com.kracz0.desktopwitelonbank.Controllers.Client.Dashboard;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.Card;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CardsService {

    public List<Card> getKarty(int kontoId) throws Exception {
        String url = ApiConfig.BASE_URL + "/konta/" + kontoId + "/karty";

        HttpRequest request = ApiClient.authorizedRequest(url).GET().build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONArray arr = new JSONArray(response.body());
            List<Card> karty = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);

                String typ = json.optString("typ_karty", "VISA");
                String masked = json.optString("nr_karty_masked", "**** **** **** ????");
                String data = json.optString("data_waznosci", "0000-00-00");
                boolean zablokowana = json.optBoolean("zablokowana", false);
                double limit = json.optDouble("limit_dzienny", 0.0);

                karty.add(new Card(typ, masked, data, zablokowana, limit));
            }

            return karty;
        } else {
            throw new Exception("Błąd pobierania kart: " + response.body());
        }
    }
}


