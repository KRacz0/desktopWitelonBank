package com.kracz0.desktopwitelonbank.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecipientTest {
    @Test
    public void testSettersAndGetters() {
        Recipient r = new Recipient();
        r.setId(1);
        r.setNazwa_zdefiniowana("Krystian");
        r.setNr_konta("111");
        r.setRzeczywista_nazwa("Krystian Raczynski");
        r.setAdres_linia1("ul. Test 1");
        r.setAdres_linia2("00-000 Miasto");
        r.setDodano("2024-01-01");

        assertEquals(1, r.getId());
        assertEquals("Krystian", r.getNazwa_zdefiniowana());
        assertEquals("111", r.getNr_konta());
        assertEquals("Krystian Raczynski", r.getRzeczywista_nazwa());
        assertEquals("ul. Test 1", r.getAdres_linia1());
        assertEquals("00-000 Miasto", r.getAdres_linia2());
        assertEquals("2024-01-01", r.getDodano());
    }
}