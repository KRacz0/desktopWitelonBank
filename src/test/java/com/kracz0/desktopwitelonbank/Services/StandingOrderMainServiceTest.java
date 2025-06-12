package com.kracz0.desktopwitelonbank.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kracz0.desktopwitelonbank.Models.DTO.StandingOrder;
import com.kracz0.desktopwitelonbank.Utils.ApiClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class StandingOrderMainServiceTest {

    private MockWebServer mockWebServer;
    private StandingOrderMainService service;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        baseUrl = mockWebServer.url("/api/zlecenia-stale").toString();
        service = new TestStandingOrderMainService(baseUrl);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void createStandingOrder_shouldReturnTrueOn201() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(201));
        StandingOrder order = new StandingOrder();
        order.setId_uzytkownika(1);
        order.setId_konta_zrodlowego(101);
        order.setNr_konta_docelowego("12345678901234567890123456");
        order.setNazwa_odbiorcy("Jan Kowalski");
        order.setTytul_przelewu("Opłata za usługi");
        order.setKwota(250.0);
        order.setCzestotliwosc("MIESIECZNIE");
        order.setData_startu("2025-06-01");
        order.setData_nastepnego_wykonania("2025-07-01");
        order.setData_zakonczenia("2026-06-01");
        order.setAktywne(true);

        assertTrue(service.createStandingOrder(order));
    }

    @Test
    void updateStandingOrder_shouldReturnTrueOn200() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        StandingOrder updated = new StandingOrder();
        updated.setId_uzytkownika(2);
        updated.setId_konta_zrodlowego(202);
        updated.setNr_konta_docelowego("11112222333344445555666677");
        updated.setNazwa_odbiorcy("Zmieniony Odbiorca");
        updated.setTytul_przelewu("Nowy tytuł");
        updated.setKwota(500.0);
        updated.setCzestotliwosc("TYGODNIOWO");
        updated.setData_startu("2025-06-10");
        updated.setData_nastepnego_wykonania("2025-08-01");
        updated.setData_zakonczenia("2025-12-31");
        updated.setAktywne(false);

        assertTrue(service.updateStandingOrder(2, updated));
    }

    @Test
    void deleteStandingOrder_shouldReturnTrueOn200() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        assertTrue(service.deleteStandingOrder(3));
    }

    static class TestStandingOrderMainService extends StandingOrderMainService {
        private final String testUrl;

        public TestStandingOrderMainService(String testUrl) {
            super();
            this.testUrl = testUrl;
        }

        @Override
        public boolean createStandingOrder(StandingOrder newOrder) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String body = mapper.writeValueAsString(newOrder);
                HttpRequest request = ApiClient.authorizedRequest(testUrl)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .header("Content-Type", "application/json")
                        .build();
                HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());
                return response.statusCode() == 201;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean updateStandingOrder(int id, StandingOrder updatedOrder) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String body = mapper.writeValueAsString(updatedOrder);
                HttpRequest request = ApiClient.authorizedRequest(testUrl + "/" + id)
                        .PUT(HttpRequest.BodyPublishers.ofString(body))
                        .header("Content-Type", "application/json")
                        .build();
                HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());
                return response.statusCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean deleteStandingOrder(int id) {
            try {
                HttpRequest request = ApiClient.authorizedRequest(testUrl + "/" + id)
                        .DELETE()
                        .build();
                HttpResponse<String> response = ApiClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());
                return response.statusCode() == 200;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
