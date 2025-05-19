package com.kracz0.desktopwitelonbank.Utils;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;

public class HttpUtil {
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static HttpClient getClient() {
        return client;
    }

    public static HttpRequest.Builder request(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json");
    }
}
