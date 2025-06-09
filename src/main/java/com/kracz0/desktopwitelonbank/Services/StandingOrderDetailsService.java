package com.kracz0.desktopwitelonbank.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.StandingOrder;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StandingOrderDetailsService {

    public StandingOrder getZlecenieStaleById(int id) {
        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZLECENIA_STALE + "/" + id)
                    .GET().build();

            HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(response.body(), StandingOrder.class);
            } else {
                System.err.println("Błąd pobierania szczegółów zlecenia: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Wyjątek przy pobieraniu szczegółów zlecenia:");
            e.printStackTrace();
        }
        return null;
    }
}

