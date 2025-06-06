package com.kracz0.desktopwitelonbank.Models.DTO;

public class AccountAdmin {
    private int id;
    private String numerKonta;
    private double aktualneSaldo;
    private String waluta;
    private double limitPrzelewuDzienny;
    private boolean czyZablokowane;
    private String utworzono;
    private String imie;
    private String nazwisko;
    private String email;
    private String telefon;

    public AccountAdmin() { }

    // Konstruktr
    public AccountAdmin(int id, String numerKonta, double aktualneSaldo, String waluta,
                        double limitPrzelewuDzienny, boolean czyZablokowane, String utworzono,
                        String imie, String nazwisko, String email, String telefon) {
        this.id = id;
        this.numerKonta = numerKonta;
        this.aktualneSaldo = aktualneSaldo;
        this.waluta = waluta;
        this.limitPrzelewuDzienny = limitPrzelewuDzienny;
        this.czyZablokowane = czyZablokowane;
        this.utworzono = utworzono;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.email = email;
        this.telefon = telefon;
    }

    public int getId() { return id; }
    public String getNumerKonta() { return numerKonta; }
    public double getAktualneSaldo() { return aktualneSaldo; }
    public String getWaluta() { return waluta; }
    public double getLimitPrzelewuDzienny() { return limitPrzelewuDzienny; }
    public boolean isCzyZablokowane() { return czyZablokowane; }
    public String getUtworzono() { return utworzono; }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getEmail() { return email; }
    public String getTelefon() { return telefon; }
    public void setCzyZablokowane(boolean czyZablokowane) {
        this.czyZablokowane = czyZablokowane;
    }
    public void setLimitPrzelewuDzienny(double limit) {
        this.limitPrzelewuDzienny = limit;
    }


}


