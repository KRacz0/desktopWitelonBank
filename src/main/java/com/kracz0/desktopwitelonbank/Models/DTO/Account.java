package com.kracz0.desktopwitelonbank.Models.DTO;

public class Account {
    private int id;
    private String nrKonta;
    private double saldo;
    private String waluta;

    public Account(int id, String nrKonta, double saldo, String waluta) {
        this.id = id;
        this.nrKonta = nrKonta;
        this.saldo = saldo;
        this.waluta = waluta;
    }

    public int getId() {
        return id;
    }

    public String getNrKonta() {
        return nrKonta;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getWaluta() {
        return waluta;
    }

    public void setId(int id) {
        this.id = id;
    }

}
