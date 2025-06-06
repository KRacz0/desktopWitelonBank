package com.kracz0.desktopwitelonbank.Config;

public class ApiConfig {
    public static final String BASE_URL = "https://witelonapi.host358482.xce.pl/api";

    public static final String LOGIN = BASE_URL + "/login";
    public static final String LOGOUT = BASE_URL + "/logout";
    public static final String TWO_FACTOR = BASE_URL + "/2fa";
    public static final String PRZELEWY = BASE_URL + "/przelewy";
    public static final String KRYPTOWALUTY_CENY = BASE_URL + "/kryptowaluty/ceny";
    public static final String PORTFEL = BASE_URL + "/portfel";
    public static final String INWESTYCJE_KUP = BASE_URL + "/inwestycje/kup";
    public static final String INWESTYCJE_SPRZEDAJ = BASE_URL + "/inwestycje/sprzedaj";
    public static final String ZAPISANI_ODBIORCY = BASE_URL + "/zapisani-odbiorcy";
    public static final String ZLECENIA_STALE = BASE_URL + "/zlecenia-stale";

    public static final String ADMIN_KONTA = BASE_URL + "/admin/konta";
    public static final String ADMIN_PRZELEWY = BASE_URL + "/admin/przelewy";
    public static final String ADMIN_STATYSTYKI = BASE_URL + "/admin/statystyki";
    public static final String ADMIN_RAPORTY = BASE_URL + "/admin/raporty/przelewy";
}

