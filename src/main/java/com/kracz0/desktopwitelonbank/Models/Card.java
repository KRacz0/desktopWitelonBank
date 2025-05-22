package com.kracz0.desktopwitelonbank.Models;

public class Card {
    private String typKarty;
    private String nrKarty;
    private String dataWaznosci;
    private boolean zablokowana;
    private double limitDzienny;

    public Card(String typKarty, String nrKarty, String dataWaznosci, boolean zablokowana, double limitDzienny) {
        this.typKarty = typKarty;
        this.nrKarty = nrKarty;
        this.dataWaznosci = dataWaznosci;
        this.zablokowana = zablokowana;
        this.limitDzienny = limitDzienny;
    }

    public String getTypKarty() { return typKarty; }
    public String getNrKarty() { return nrKarty; }
    public String getDataWaznosci() { return dataWaznosci; }
    public boolean isZablokowana() { return zablokowana; }
    public double getLimitDzienny() { return limitDzienny; }
}

