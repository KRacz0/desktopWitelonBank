package com.kracz0.desktopwitelonbank.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {
    @Test
    public void testConstructorAndGetters() {
        Card card = new Card("VISA", "1234", "2030-01", true, 1000.0);
        assertEquals("VISA", card.getTypKarty());
        assertEquals("1234", card.getNrKarty());
        assertEquals("2030-01", card.getDataWaznosci());
        assertTrue(card.isZablokowana());
        assertEquals(1000.0, card.getLimitDzienny());
    }
}