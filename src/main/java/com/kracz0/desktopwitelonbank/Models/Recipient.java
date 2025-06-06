package com.kracz0.desktopwitelonbank.Models;

public class Recipient {
    private int id;
    private String nazwa_zdefiniowana;
    private String nr_konta;
    private String rzeczywista_nazwa;
    private String adres_linia1;
    private String adres_linia2;
    private String dodano;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNazwa_zdefiniowana() { return nazwa_zdefiniowana; }
    public void setNazwa_zdefiniowana(String nazwa_zdefiniowana) { this.nazwa_zdefiniowana = nazwa_zdefiniowana; }

    public String getNr_konta() { return nr_konta; }
    public void setNr_konta(String nr_konta) { this.nr_konta = nr_konta; }

    public String getRzeczywista_nazwa() { return rzeczywista_nazwa; }
    public void setRzeczywista_nazwa(String rzeczywista_nazwa) { this.rzeczywista_nazwa = rzeczywista_nazwa; }

    public String getAdres_linia1() { return adres_linia1; }
    public void setAdres_linia1(String adres_linia1) { this.adres_linia1 = adres_linia1; }

    public String getAdres_linia2() { return adres_linia2; }
    public void setAdres_linia2(String adres_linia2) { this.adres_linia2 = adres_linia2; }

    public String getDodano() { return dodano; }
    public void setDodano(String dodano) { this.dodano = dodano; }
}


