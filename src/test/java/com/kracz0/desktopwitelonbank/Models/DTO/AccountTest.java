package com.kracz0.desktopwitelonbank.Models.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    @Test
    public void testConstructorAndGetters() {
        Account acc = new Account(1, "123", 50.0, "PLN");
        assertEquals(1, acc.getId());
        assertEquals("123", acc.getNrKonta());
        assertEquals(50.0, acc.getSaldo());
        assertEquals("PLN", acc.getWaluta());
    }

    @Test
    public void testSetId() {
        Account acc = new Account(1, "123", 50.0, "PLN");
        acc.setId(2);
        assertEquals(2, acc.getId());
    }
}