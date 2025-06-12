package com.kracz0.desktopwitelonbank.Services;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceTest {

    private MockWebServer mockWebServer;

    private String urlKup;
    private String urlSprzedaj;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        urlKup = mockWebServer.url("/api/inwestycje/kup").toString();
        urlSprzedaj = mockWebServer.url("/api/inwestycje/sprzedaj").toString();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    private String performBuy(int accountId, String symbol, double kwotaPLN) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("id_konta_pln", accountId);
            payload.put("symbol_krypto", symbol);
            payload.put("kwota_pln", kwotaPLN);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlKup))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new JSONObject(response.body()).getString("message");
            } else {
                return new JSONObject(response.body()).optString("message", "Nieznany błąd.");
            }
        } catch (Exception e) {
            return "Błąd połączenia z API.";
        }
    }

    private String performSell(int accountId, String symbol, double iloscKrypto) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("id_konta_pln", accountId);
            payload.put("symbol_krypto", symbol);
            payload.put("ilosc_krypto", iloscKrypto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlSprzedaj))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new JSONObject(response.body()).getString("message");
            } else {
                return new JSONObject(response.body()).optString("message", "Nieznany błąd.");
            }
        } catch (Exception e) {
            return "Błąd połączenia z API.";
        }
    }

    @Test
    void buyCrypto_shouldReturnSuccessMessage() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"message\":\"Zakup udany\"}")
                .addHeader("Content-Type", "application/json"));

        String result = performBuy(1, "BTC", 100.0);
        assertEquals("Zakup udany", result);
    }

    @Test
    void buyCrypto_shouldReturnErrorMessage() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"message\":\"Brak środków\"}")
                .addHeader("Content-Type", "application/json"));

        String result = performBuy(1, "BTC", 9999.0);
        assertEquals("Brak środków", result);
    }

    @Test
    void sellCrypto_shouldReturnSuccessMessage() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"message\":\"Sprzedaż zakończona\"}")
                .addHeader("Content-Type", "application/json"));

        String result = performSell(1, "ETH", 0.5);
        assertEquals("Sprzedaż zakończona", result);
    }

    @Test
    void sellCrypto_shouldReturnErrorMessage() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"message\":\"Za mało kryptowaluty\"}")
                .addHeader("Content-Type", "application/json"));

        String result = performSell(1, "ETH", 999.0);
        assertEquals("Za mało kryptowaluty", result);
    }

    @Test
    void buyCrypto_shouldHandleConnectionError() {
        try {
            mockWebServer.shutdown();
        } catch (Exception ignored) {}

        String result = performBuy(1, "BTC", 50.0);
        assertEquals("Błąd połączenia z API.", result);
    }

    @Test
    void sellCrypto_shouldHandleConnectionError() {
        try {
            mockWebServer.shutdown();
        } catch (Exception ignored) {}

        String result = performSell(1, "ETH", 1.0);
        assertEquals("Błąd połączenia z API.", result);
    }
}
