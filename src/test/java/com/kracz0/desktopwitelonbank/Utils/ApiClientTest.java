package com.kracz0.desktopwitelonbank.Utils;

import com.kracz0.desktopwitelonbank.Models.Model;
import com.kracz0.desktopwitelonbank.Models.User;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ApiClientTest {
    @Test
    public void testAuthorizedRequestIncludesToken() {
        Model model = Model.getInstance();
        User user = new User();
        user.setToken("testtoken");
        model.setLoggedUser(user);

        HttpRequest request = ApiClient.authorizedRequest("https://witelonapi.host358482.xce.pl/api").GET().build();

        assertEquals(URI.create("https://witelonapi.host358482.xce.pl/api"), request.uri());
        Optional<String> header = request.headers().firstValue("Authorization");
        assertTrue(header.isPresent());
        assertEquals("Bearer testtoken", header.get());
    }

    @Test
    public void testBasicRequestHeaders() {
        HttpRequest request = ApiClient.basicRequest("https://witelonapi.host358482.xce.pl/api").build();
        assertEquals("application/json", request.headers().firstValue("Content-Type").orElse(null));
        assertEquals("application/json", request.headers().firstValue("Accept").orElse(null));
    }

    @Test
    public void testHttpUtilRequest() {
        HttpRequest request = HttpUtil.request("https://witelonapi.host358482.xce.pl/api").build();
        assertEquals("application/json", request.headers().firstValue("Content-Type").orElse(null));
    }
}