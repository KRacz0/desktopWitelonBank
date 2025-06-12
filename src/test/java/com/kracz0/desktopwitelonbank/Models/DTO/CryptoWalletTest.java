package com.kracz0.desktopwitelonbank.Models.DTO;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class CryptoWalletTest {
    @Test
    public void testGettersUsingReflection() throws Exception {
        CryptoWallet wallet = new CryptoWallet();
        Field btc = CryptoWallet.class.getDeclaredField("saldo_bitcoin");
        Field eth = CryptoWallet.class.getDeclaredField("saldo_ethereum");
        btc.setAccessible(true);
        eth.setAccessible(true);
        btc.set(wallet, 3.0);
        eth.set(wallet, 4.0);
        assertEquals(3.0, wallet.getSaldoBitcoin());
        assertEquals(4.0, wallet.getSaldoEthereum());
    }
}