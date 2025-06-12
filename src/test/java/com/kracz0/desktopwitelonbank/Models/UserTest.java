package com.kracz0.desktopwitelonbank.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    public void testSettersAndGetters() {
        User u = new User();
        u.setId(5);
        u.setImie("Krystian");
        u.setNazwisko("Raczynski");
        u.setEmail("kr@gmail.pl");
        u.setAdministrator(true);
        u.setToken("abc");

        assertEquals(5, u.getId());
        assertEquals("Krystian", u.getImie());
        assertEquals("Raczynski", u.getNazwisko());
        assertEquals("kr@gmail.pl", u.getEmail());
        assertTrue(u.isAdministrator());
        assertEquals("abc", u.getToken());
    }
}