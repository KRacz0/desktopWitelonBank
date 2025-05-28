package com.kracz0.desktopwitelonbank.Models.DTO;

public class AccountAdmin {
    private int id;
    private String numerKonta;
    private double aktualneSaldo;
    private String waluta;
    private double limitPrzelewuDzienny;
    private boolean czyZablokowane;
    private String utworzono;

    public AccountAdmin() { }

    public AccountAdmin(int id, String numerKonta, double aktualneSaldo, String waluta,
                        double limitPrzelewuDzienny, boolean czyZablokowane, String utworzono) {
        this.id = id;
        this.numerKonta = numerKonta;
        this.aktualneSaldo = aktualneSaldo;
        this.waluta = waluta;
        this.limitPrzelewuDzienny = limitPrzelewuDzienny;
        this.czyZablokowane = czyZablokowane;
        this.utworzono = utworzono;
    }

    public int getId() { return id; }
    public String getNumerKonta() { return numerKonta; }
    public double getAktualneSaldo() { return aktualneSaldo; }
    public String getWaluta() { return waluta; }
    public double getLimitPrzelewuDzienny() { return limitPrzelewuDzienny; }
    public boolean isCzyZablokowane() { return czyZablokowane; }
    public String getUtworzono() { return utworzono; }
}


