package com.kracz0.desktopwitelonbank.Services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.DTO.StandingOrder;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import org.json.JSONArray;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class StandingOrderMainService {

    public List<StandingOrder> getZleceniaStale() {
        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZLECENIA_STALE)
                    .GET()
                    .build();

            HttpResponse<String> response = ApiClient.getClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONArray jsonArray = new JSONArray(response.body());
                ObjectMapper mapper = new ObjectMapper();
                List<StandingOrder> orders = mapper.readValue(jsonArray.toString(),
                        new TypeReference<List<StandingOrder>>() {});

                System.out.println("Liczba zmapowanych zleceń: " + orders.size());
                for (StandingOrder order : orders) {
                    System.out.println("Zlecenie: " + order.getNazwa_odbiorcy() + ", kwota: " + order.getKwota() +
                            ", aktywne: " + order.isAktywne());
                }

                return orders;
            } else {
                System.err.println("Błąd odpowiedzi API: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas pobierania zleceń stałych:");
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public boolean updateStandingOrder(int id, StandingOrder updatedOrder) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(updatedOrder);

            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZLECENIA_STALE + "/" + id)
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = ApiClient.getClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Błąd podczas aktualizacji zlecenia:");
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStandingOrder(int id) {
        try {
            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZLECENIA_STALE + "/" + id)
                    .DELETE()
                    .build();

            HttpResponse<String> response = ApiClient.getClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Błąd podczas usuwania zlecenia:");
            e.printStackTrace();
        }
        return false;
    }

    public boolean createStandingOrder(StandingOrder newOrder) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(newOrder);

            HttpRequest request = ApiClient.authorizedRequest(ApiConfig.ZLECENIA_STALE)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = ApiClient.getClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 201;
        } catch (Exception e) {
            System.err.println("Błąd podczas tworzenia zlecenia:");
            e.printStackTrace();
        }
        return false;
    }
}

