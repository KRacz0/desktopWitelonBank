package com.kracz0.desktopwitelonbank.Models.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StandingOrderTest {
    @Test
    public void testAllSettersAndGetters() {
        StandingOrder o = new StandingOrder();
        o.setId(1);
        o.setId_uzytkownika(2);
        o.setId_konta_zrodlowego(3);
        o.setNr_konta_docelowego("111");
        o.setNazwa_odbiorcy("Krystian");
        o.setTytul_przelewu("test");
        o.setKwota(5.0);
        o.setCzestotliwosc("MIESIAC");
        o.setData_startu("2024");
        o.setData_nastepnego_wykonania("2024");
        o.setData_zakonczenia("2025");
        o.setAktywne(true);

        assertEquals(1, o.getId());
        assertEquals(2, o.getId_uzytkownika());
        assertEquals(3, o.getId_konta_zrodlowego());
        assertEquals("111", o.getNr_konta_docelowego());
        assertEquals("Krystian", o.getNazwa_odbiorcy());
        assertEquals("test", o.getTytul_przelewu());
        assertEquals(5.0, o.getKwota());
        assertEquals("MIESIAC", o.getCzestotliwosc());
        assertEquals("2024", o.getData_startu());
        assertEquals("2024", o.getData_nastepnego_wykonania());
        assertEquals("2025", o.getData_zakonczenia());
        assertTrue(o.isAktywne());
    }
}