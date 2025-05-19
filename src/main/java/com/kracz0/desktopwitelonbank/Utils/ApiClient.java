package com.kracz0.desktopwitelonbank.Utils;

import com.kracz0.desktopwitelonbank.Models.Model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class ApiClient {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static HttpClient getClient() {
        return client;
    }

    public static HttpRequest.Builder authorizedRequest(String url) {
        String token = Model.getInstance().getAuthToken();
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token);
    }

    public static HttpRequest.Builder basicRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }
}
