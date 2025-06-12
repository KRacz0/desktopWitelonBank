package com.kracz0.desktopwitelonbank.Models.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountAdminTest {
    @Test
    public void testConstructorAndGetters() {
        AccountAdmin a = new AccountAdmin(1, "111", 0.0, "PLN", 10.0, true, "now", "Jan", "K", "e", "t");
        assertEquals(1, a.getId());
        assertEquals("111", a.getNumerKonta());
        assertEquals(0.0, a.getAktualneSaldo());
        assertEquals("PLN", a.getWaluta());
        assertEquals(10.0, a.getLimitPrzelewuDzienny());
        assertTrue(a.isCzyZablokowane());
        assertEquals("now", a.getUtworzono());
        assertEquals("Jan", a.getImie());
        assertEquals("K", a.getNazwisko());
        assertEquals("e", a.getEmail());
        assertEquals("t", a.getTelefon());
    }

    @Test
    public void testSetters() {
        AccountAdmin a = new AccountAdmin();
        a.setCzyZablokowane(true);
        a.setLimitPrzelewuDzienny(5.0);
        assertTrue(a.isCzyZablokowane());
        assertEquals(5.0, a.getLimitPrzelewuDzienny());
    }
}