package com.kracz0.desktopwitelonbank.Models.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdminStatsTest {
    @Test
    public void testSettersAndGetters() {
        AdminStats stats = new AdminStats();
        stats.setCalkowitaLiczbaUzytkownikow(10);
        stats.setLiczbaZweryfikowanych(5);
        stats.setSumaBTC(1.5);
        stats.setSumaETH(2.5);

        assertEquals(10, stats.getCalkowitaLiczbaUzytkownikow());
        assertEquals(5, stats.getLiczbaZweryfikowanych());
        assertEquals(1.5, stats.getSumaBTC());
        assertEquals(2.5, stats.getSumaETH());
    }
}