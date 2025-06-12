package com.kracz0.desktopwitelonbank.Models.DTO;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransferTest {
    @Test
    public void testIncomingTransfer() {
        Transfer t = new Transfer("t", 100.0, "przychodzacy", "2024-01-01", "PLN",
                "Odbiorca", "1111", "Nadawca", "2222");
        assertEquals("Nadawca", t.getNazwaDrugiejStrony());
        assertEquals("2222", t.getNrKontaDrugiejStrony());
    }

    @Test
    public void testOutgoingTransfer() {
        Transfer t = new Transfer("t", 100.0, "wychodzacy", "2024-01-01", "PLN",
                "Odbiorca", "1111", "Nadawca", "2222");
        assertEquals("Odbiorca", t.getNazwaDrugiejStrony());
        assertEquals("1111", t.getNrKontaDrugiejStrony());
    }
}