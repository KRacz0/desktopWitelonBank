package com.kracz0.desktopwitelonbank.Services;

import com.kracz0.desktopwitelonbank.Config.ApiConfig;
import com.kracz0.desktopwitelonbank.Models.User;
import com.kracz0.desktopwitelonbank.Utils.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class AuthService {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String login(String email, String haslo) throws Exception {
        String body = String.format("{\"email\":\"%s\", \"haslo\":\"%s\"}", email, haslo);
        HttpRequest request = HttpUtil.request(ApiConfig.LOGIN)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = HttpUtil.getClient().send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status == 200) {
            JsonNode json = mapper.readTree(response.body());
            return json.get("email").asText();
        } else if (status == 401 || status == 422 || status == 500) {
            throw new Exception(mapper.readTree(response.body()).get("message").asText());
        } else {
            throw new Exception("Nieoczekiwany błąd: " + response.body());
        }
    }

    public static User verify2FA(String email, String code) throws Exception {
        String body = String.format("{\"email\":\"%s\", \"dwuetapowy_kod\":\"%s\"}", email, code);
        HttpRequest request = HttpUtil.request(ApiConfig.TWO_FACTOR)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = HttpUtil.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode json = mapper.readTree(response.body());

        if (response.statusCode() == 200) {
            User user = new User();
            user.setEmail(json.get("user").get("email").asText());
            user.setImie(json.get("user").get("imie").asText());
            user.setNazwisko(json.get("user").get("nazwisko").asText());
            user.setId(json.get("user").get("id").asInt());
            user.setAdministrator(json.get("user").get("administrator").asBoolean());
            user.setToken(json.get("token").asText());
            return user;
        } else {
            throw new Exception(json.get("message").asText());
        }
    }

    public void verifyTwoFactorCode(String email, String code, AuthCallback callback) {
        new Thread(() -> {
            try {
                User user = verify2FA(email, code);
                callback.onResult(new AuthResponse(true, "Zalogowano pomyślnie.", user));
            } catch (Exception e) {
                callback.onResult(new AuthResponse(false, e.getMessage(), null));
            }
        }).start();
    }

    public class AuthResponse {
        private boolean success;
        private String message;
        private User user;

        public AuthResponse(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }


    public interface AuthCallback {
        void onResult(AuthResponse response);
    }
}
