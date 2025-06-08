package com.kracz0.desktopwitelonbank.Models.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StandingOrder {
    private int id;
    private int id_uzytkownika;
    private int id_konta_zrodlowego;
    private String nr_konta_docelowego;
    private String nazwa_odbiorcy;
    private String tytul_przelewu;
    private double kwota;
    private String czestotliwosc;
    private String data_startu;
    private String data_nastepnego_wykonania;
    private String data_zakonczenia;
    private boolean aktywne;

    // wymagane: gettery i settery dla WSZYSTKICH pól
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getId_uzytkownika() { return id_uzytkownika; }
    public void setId_uzytkownika(int id_uzytkownika) { this.id_uzytkownika = id_uzytkownika; }

    public int getId_konta_zrodlowego() { return id_konta_zrodlowego; }
    public void setId_konta_zrodlowego(int id_konta_zrodlowego) { this.id_konta_zrodlowego = id_konta_zrodlowego; }

    public String getNr_konta_docelowego() { return nr_konta_docelowego; }
    public void setNr_konta_docelowego(String nr_konta_docelowego) { this.nr_konta_docelowego = nr_konta_docelowego; }

    public String getNazwa_odbiorcy() { return nazwa_odbiorcy; }
    public void setNazwa_odbiorcy(String nazwa_odbiorcy) { this.nazwa_odbiorcy = nazwa_odbiorcy; }

    public String getTytul_przelewu() { return tytul_przelewu; }
    public void setTytul_przelewu(String tytul_przelewu) { this.tytul_przelewu = tytul_przelewu; }

    public double getKwota() { return kwota; }
    public void setKwota(double kwota) { this.kwota = kwota; }

    public String getCzestotliwosc() { return czestotliwosc; }
    public void setCzestotliwosc(String czestotliwosc) { this.czestotliwosc = czestotliwosc; }

    public String getData_startu() { return data_startu; }
    public void setData_startu(String data_startu) { this.data_startu = data_startu; }

    public String getData_nastepnego_wykonania() { return data_nastepnego_wykonania; }
    public void setData_nastepnego_wykonania(String data_nastepnego_wykonania) { this.data_nastepnego_wykonania = data_nastepnego_wykonania; }

    public String getData_zakonczenia() { return data_zakonczenia; }
    public void setData_zakonczenia(String data_zakonczenia) { this.data_zakonczenia = data_zakonczenia; }

    public boolean isAktywne() { return aktywne; }
    public void setAktywne(boolean aktywne) { this.aktywne = aktywne; }
}



