package com.kracz0.desktopwitelonbank.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.Recipient;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AddressBookService {

    private static final ObjectMapper mapper = new ObjectMapper();

    public List<Recipient> getAllRecipients() throws Exception {
        HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZAPISANI_ODBIORCY)
                .GET().build();
        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root = mapper.readTree(response.body());
            JsonNode data = root.get("data");
            return mapper.readValue(data.toString(), new TypeReference<List<Recipient>>() {});
        } else if (response.statusCode() == 401) {
            throw new Exception("Nieautoryzowany dostęp.");
        } else {
            throw new Exception("Błąd pobierania odbiorców. Kod: " + response.statusCode());
        }
    }


    public boolean addRecipient(Recipient recipient) throws Exception {
        String json = mapper.writeValueAsString(Map.of(
                "nazwa_odbiorcy_zdefiniowana", recipient.getNazwa_zdefiniowana(),
                "nr_konta_odbiorcy", recipient.getNr_konta(),
                "rzeczywista_nazwa_odbiorcy", recipient.getRzeczywista_nazwa(),
                "adres_odbiorcy_linia1", recipient.getAdres_linia1(),
                "adres_odbiorcy_linia2", recipient.getAdres_linia2()
        ));

        HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZAPISANI_ODBIORCY)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 201;
    }

    public boolean updateRecipient(Recipient recipient) throws Exception {
        String json = new ObjectMapper().writeValueAsString(Map.of(
                "nazwa_odbiorcy_zdefiniowana", recipient.getNazwa_zdefiniowana(),
                "nr_konta_odbiorcy", recipient.getNr_konta(),
                "rzeczywista_nazwa_odbiorcy", recipient.getRzeczywista_nazwa(),
                "adres_odbiorcy_linia1", recipient.getAdres_linia1(),
                "adres_odbiorcy_linia2", recipient.getAdres_linia2()
        ));

        HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZAPISANI_ODBIORCY + "/" + recipient.getId())
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }

    public boolean deleteRecipient(int id) throws Exception {
        HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZAPISANI_ODBIORCY + "/" + id)
                .DELETE()
                .build();

        HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200 || response.statusCode() == 204;
    }
}
