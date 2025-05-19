package com.kracz0.desktopwitelonbank.Models.DTO;

public class Transfer {
    private String tytul;
    private double kwota;
    private String typTransakcji; // "przychodzace" lub "wychodzace"
    private String data;

    public Transfer(String tytul, double kwota, String typTransakcji, String data) {
        this.tytul = tytul;
        this.kwota = kwota;
        this.typTransakcji = typTransakcji;
        this.data = data;
    }

    public String getTytul() {
        return tytul;
    }

    public double getKwota() {
        return kwota;
    }

    public String getTypTransakcji() {
        return typTransakcji;
    }

    public String getData() {
        return data;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public void setKwota(double kwota) {
        this.kwota = kwota;
    }

    public void setTypTransakcji(String typTransakcji) {
        this.typTransakcji = typTransakcji;
    }

    public void setData(String data) {
        this.data = data;
    }
}