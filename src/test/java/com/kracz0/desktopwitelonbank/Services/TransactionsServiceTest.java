package com.kracz0.desktopwitelonbank.Services;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionsServiceTest {
    @Test
    public void testSendTransferInvalidDataReturnsNull() {
        TransactionsService service = new TransactionsService();
        JSONObject data = new JSONObject();
        data.put("kwota", -1);
        assertNull(service.sendTransfer(data));
    }
}