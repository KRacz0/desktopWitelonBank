package com.kracz0.desktopwitelonbank.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {
    @Test
    public void testSingleton() {
        Model m1 = Model.getInstance();
        Model m2 = Model.getInstance();
        assertSame(m1, m2, "getInstance powinno zwracać tą samąinstację");
    }

    @Test
    public void testLoginLogout() {
        Model model = Model.getInstance();
        User user = new User();
        user.setToken("abc");
        model.setLoggedUser(user);
        assertTrue(model.isLoggedIn());
        assertEquals("abc", model.getAuthToken());
        model.logout();
        assertFalse(model.isLoggedIn());
        assertNull(model.getAuthToken());
    }

    @Test
    public void testOpenTransferModalFlag() {
        Model model = Model.getInstance();
        model.setOpenTransferModalFlag(true);
        assertTrue(model.shouldOpenTransferModal());
        model.setOpenTransferModalFlag(false);
        assertFalse(model.shouldOpenTransferModal());
    }
}