package com.kracz0.desktopwitelonbank.Services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AddressBookServiceTest {

    private MockWebServer mockWebServer;
    private AddressBookService service;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        baseUrl = mockWebServer.url("/api/zapisani-odbiorcy").toString();
        service = new AddressBookService(baseUrl);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getAllRecipients_shouldReturnList() throws Exception {
        String responseJson = """
            {
              "data": [
                {
                  "id": 1,
                  "nazwa_zdefiniowana": "Dom",
                  "nr_konta": "1234567890",
                  "rzeczywista_nazwa": "Krystian Raczynski",
                  "adres_linia1": "ul. Kwiatowa 1",
                  "adres_linia2": "59-800 Luban"
                }
              ]
            }
        """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson)
                .addHeader("Content-Type", "application/json"));

        List<Recipient> recipients = service.getAllRecipients();

        assertEquals(1, recipients.size());
        assertEquals("Dom", recipients.get(0).nazwaZdefiniowana);
        assertEquals("Jan Kowalski", recipients.get(0).rzeczywistaNazwa);
    }

    @Test
    void addRecipient_shouldReturnTrueOn201() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(201));

        Recipient r = new Recipient(0, "Test", "123", "Adam", "Test 1", "00-000");

        assertTrue(service.addRecipient(r));
    }

    @Test
    void updateRecipient_shouldReturnTrueOn200() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        Recipient r = new Recipient(5, "Zaktualizowany", "999", "Nowak", "ul. X", "Miasto");
        assertTrue(service.updateRecipient(r));
    }

    @Test
    void deleteRecipient_shouldReturnTrueOn204() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));
        assertTrue(service.deleteRecipient(3));
    }

    static class Recipient {
        public int id;
        @JsonProperty("nazwa_zdefiniowana")
        public String nazwaZdefiniowana;
        @JsonProperty("nr_konta")
        public String nrKonta;
        @JsonProperty("rzeczywista_nazwa")
        public String rzeczywistaNazwa;
        @JsonProperty("adres_linia1")
        public String adresLinia1;
        @JsonProperty("adres_linia2")
        public String adresLinia2;

        public Recipient() {}

        public Recipient(int id, String nazwa, String konto, String imie, String adr1, String adr2) {
            this.id = id;
            this.nazwaZdefiniowana = nazwa;
            this.nrKonta = konto;
            this.rzeczywistaNazwa = imie;
            this.adresLinia1 = adr1;
            this.adresLinia2 = adr2;
        }
    }

    static class AddressBookService {
        private final String baseUrl;
        private static final ObjectMapper mapper = new ObjectMapper();

        public AddressBookService(String url) {
            this.baseUrl = url;
        }

        public List<Recipient> getAllRecipients() throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                var root = mapper.readTree(response.body()).get("data");
                return mapper.readValue(root.toString(), new TypeReference<>() {});
            } else if (response.statusCode() == 401) {
                throw new Exception("Nieautoryzowany dostęp.");
            } else {
                throw new Exception("Błąd pobierania odbiorców. Kod: " + response.statusCode());
            }
        }

        public boolean addRecipient(Recipient r) throws Exception {
            String json = mapper.writeValueAsString(Map.of(
                    "nazwa_odbiorcy_zdefiniowana", r.nazwaZdefiniowana,
                    "nr_konta_odbiorcy", r.nrKonta,
                    "rzeczywista_nazwa_odbiorcy", r.rzeczywistaNazwa,
                    "adres_odbiorcy_linia1", r.adresLinia1,
                    "adres_odbiorcy_linia2", r.adresLinia2
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 201;
        }

        public boolean updateRecipient(Recipient r) throws Exception {
            String json = mapper.writeValueAsString(Map.of(
                    "nazwa_odbiorcy_zdefiniowana", r.nazwaZdefiniowana,
                    "nr_konta_odbiorcy", r.nrKonta,
                    "rzeczywista_nazwa_odbiorcy", r.rzeczywistaNazwa,
                    "adres_odbiorcy_linia1", r.adresLinia1,
                    "adres_odbiorcy_linia2", r.adresLinia2
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + r.id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200;
        }

        public boolean deleteRecipient(int id) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 204;
        }
    }
}
