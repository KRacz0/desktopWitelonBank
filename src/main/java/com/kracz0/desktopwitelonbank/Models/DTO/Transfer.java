package com.kracz0.desktopwitelonbank.Models.DTO;

public class Transfer {
    private String tytul;
    private double kwota;
    private String typTransakcji; // "przychodzacy" lub "wychodzacy"
    private String data;
    private String waluta;
    private String nazwaOdbiorcy;
    private String nrKontaOdbiorcy;
    private String nazwaNadawcy;
    private String nrKontaNadawcy;

    public Transfer(String tytul, double kwota, String typTransakcji, String data, String waluta,
                    String nazwaOdbiorcy, String nrKontaOdbiorcy,
                    String nazwaNadawcy, String nrKontaNadawcy) {
        this.tytul = tytul;
        this.kwota = kwota;
        this.typTransakcji = typTransakcji;
        this.data = data;
        this.waluta = waluta;
        this.nazwaOdbiorcy = nazwaOdbiorcy;
        this.nrKontaOdbiorcy = nrKontaOdbiorcy;
        this.nazwaNadawcy = nazwaNadawcy;
        this.nrKontaNadawcy = nrKontaNadawcy;
    }

    public String getTytul() { return tytul; }
    public double getKwota() { return kwota; }
    public String getTypTransakcji() { return typTransakcji; }
    public String getData() { return data; }
    public String getWaluta() { return waluta; }

    public String getNazwaDrugiejStrony() {
        return typTransakcji.equals("przychodzacy") ? nazwaNadawcy : nazwaOdbiorcy;
    }

    public String getNrKontaDrugiejStrony() {
        return typTransakcji.equals("przychodzacy") ? nrKontaNadawcy : nrKontaOdbiorcy;
    }
}

