package com.kracz0.desktopwitelonbank.Models.DTO;

public class StandingOrder {
    private int id;
    private int idUzytkownika;
    private int idKontaZrodlowego;
    private String nrKontaDocelowego;
    private String nazwaOdbiorcy;
    private String tytulPrzelewu;
    private double kwota;
    private String czestotliwosc;
    private String dataStartu;
    private String dataNastepnegoWykonania;
    private String dataZakonczenia;
    private boolean aktywne;

    public String getNazwaOdbiorcy() {
        return nazwaOdbiorcy;
    }

    public double getKwota() {
        return kwota;
    }

    public String getDataNastepnegoWykonania() {
        return dataNastepnegoWykonania;
    }
}

