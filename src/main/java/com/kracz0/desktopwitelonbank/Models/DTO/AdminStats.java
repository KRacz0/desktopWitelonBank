package com.kracz0.desktopwitelonbank.Models.DTO;

public class AdminStats {
    // Użytkownicy
    private int calkowitaLiczbaUzytkownikow;
    private int liczbaZweryfikowanych;
    private int nowiUzytkownicyOstatnie30Dni;

    // Konta bankowe
    private int calkowitaLiczbaKont;
    private double sumaSaldoPLN;
    private int liczbaKontPLN;
    private double sredniaSaldoPLN;
    private int liczbaZablokowanychKont;
    private int liczbaKontZLimitem;

    // Karty płatnicze
    private int calkowitaLiczbaKart;
    private int liczbaZablokowanychKart;
    private int liczbaAktywneZblizeniowe;
    private int liczbaAktywneInternetowe;
    private int liczbaKartVisaDebit;

    // Przelewy
    private int calkowitaLiczbaPrzelewow;
    private double sumaKwotPrzelewowPLN;
    private int liczbaPrzelewowPLN;
    private int przelewy24h;
    private int przelewy7dni;
    private int przelewy30dni;
    private int liczbaZrealizowanych;

    // Kryptowaluty
    private int liczbaPortfeli;
    private int liczbaUzytkownikowZPortfelami;
    private double sumaBTC;
    private double sumaETH;

    public int getCalkowitaLiczbaUzytkownikow() {
        return calkowitaLiczbaUzytkownikow;
    }

    public void setCalkowitaLiczbaUzytkownikow(int calkowitaLiczbaUzytkownikow) {
        this.calkowitaLiczbaUzytkownikow = calkowitaLiczbaUzytkownikow;
    }

    public int getLiczbaZweryfikowanych() {
        return liczbaZweryfikowanych;
    }

    public void setLiczbaZweryfikowanych(int liczbaZweryfikowanych) {
        this.liczbaZweryfikowanych = liczbaZweryfikowanych;
    }

    public int getNowiUzytkownicyOstatnie30Dni() {
        return nowiUzytkownicyOstatnie30Dni;
    }

    public void setNowiUzytkownicyOstatnie30Dni(int nowiUzytkownicyOstatnie30Dni) {
        this.nowiUzytkownicyOstatnie30Dni = nowiUzytkownicyOstatnie30Dni;
    }

    public int getCalkowitaLiczbaKont() {
        return calkowitaLiczbaKont;
    }

    public void setCalkowitaLiczbaKont(int calkowitaLiczbaKont) {
        this.calkowitaLiczbaKont = calkowitaLiczbaKont;
    }

    public double getSumaSaldoPLN() {
        return sumaSaldoPLN;
    }

    public void setSumaSaldoPLN(double sumaSaldoPLN) {
        this.sumaSaldoPLN = sumaSaldoPLN;
    }

    public int getLiczbaKontPLN() {
        return liczbaKontPLN;
    }

    public void setLiczbaKontPLN(int liczbaKontPLN) {
        this.liczbaKontPLN = liczbaKontPLN;
    }

    public double getSredniaSaldoPLN() {
        return sredniaSaldoPLN;
    }

    public void setSredniaSaldoPLN(double sredniaSaldoPLN) {
        this.sredniaSaldoPLN = sredniaSaldoPLN;
    }

    public int getLiczbaZablokowanychKont() {
        return liczbaZablokowanychKont;
    }

    public void setLiczbaZablokowanychKont(int liczbaZablokowanychKont) {
        this.liczbaZablokowanychKont = liczbaZablokowanychKont;
    }

    public int getLiczbaKontZLimitem() {
        return liczbaKontZLimitem;
    }

    public void setLiczbaKontZLimitem(int liczbaKontZLimitem) {
        this.liczbaKontZLimitem = liczbaKontZLimitem;
    }

    public int getCalkowitaLiczbaKart() {
        return calkowitaLiczbaKart;
    }

    public void setCalkowitaLiczbaKart(int calkowitaLiczbaKart) {
        this.calkowitaLiczbaKart = calkowitaLiczbaKart;
    }

    public int getLiczbaZablokowanychKart() {
        return liczbaZablokowanychKart;
    }

    public void setLiczbaZablokowanychKart(int liczbaZablokowanychKart) {
        this.liczbaZablokowanychKart = liczbaZablokowanychKart;
    }

    public int getLiczbaAktywneZblizeniowe() {
        return liczbaAktywneZblizeniowe;
    }

    public void setLiczbaAktywneZblizeniowe(int liczbaAktywneZblizeniowe) {
        this.liczbaAktywneZblizeniowe = liczbaAktywneZblizeniowe;
    }

    public int getLiczbaAktywneInternetowe() {
        return liczbaAktywneInternetowe;
    }

    public void setLiczbaAktywneInternetowe(int liczbaAktywneInternetowe) {
        this.liczbaAktywneInternetowe = liczbaAktywneInternetowe;
    }

    public int getLiczbaKartVisaDebit() {
        return liczbaKartVisaDebit;
    }

    public void setLiczbaKartVisaDebit(int liczbaKartVisaDebit) {
        this.liczbaKartVisaDebit = liczbaKartVisaDebit;
    }

    public int getCalkowitaLiczbaPrzelewow() {
        return calkowitaLiczbaPrzelewow;
    }

    public void setCalkowitaLiczbaPrzelewow(int calkowitaLiczbaPrzelewow) {
        this.calkowitaLiczbaPrzelewow = calkowitaLiczbaPrzelewow;
    }

    public double getSumaKwotPrzelewowPLN() {
        return sumaKwotPrzelewowPLN;
    }

    public void setSumaKwotPrzelewowPLN(double sumaKwotPrzelewowPLN) {
        this.sumaKwotPrzelewowPLN = sumaKwotPrzelewowPLN;
    }

    public int getLiczbaPrzelewowPLN() {
        return liczbaPrzelewowPLN;
    }

    public void setLiczbaPrzelewowPLN(int liczbaPrzelewowPLN) {
        this.liczbaPrzelewowPLN = liczbaPrzelewowPLN;
    }

    public int getPrzelewy24h() {
        return przelewy24h;
    }

    public void setPrzelewy24h(int przelewy24h) {
        this.przelewy24h = przelewy24h;
    }

    public int getPrzelewy7dni() {
        return przelewy7dni;
    }

    public void setPrzelewy7dni(int przelewy7dni) {
        this.przelewy7dni = przelewy7dni;
    }

    public int getPrzelewy30dni() {
        return przelewy30dni;
    }

    public void setPrzelewy30dni(int przelewy30dni) {
        this.przelewy30dni = przelewy30dni;
    }

    public int getLiczbaZrealizowanych() {
        return liczbaZrealizowanych;
    }

    public void setLiczbaZrealizowanych(int liczbaZrealizowanych) {
        this.liczbaZrealizowanych = liczbaZrealizowanych;
    }

    public int getLiczbaPortfeli() {
        return liczbaPortfeli;
    }

    public void setLiczbaPortfeli(int liczbaPortfeli) {
        this.liczbaPortfeli = liczbaPortfeli;
    }

    public int getLiczbaUzytkownikowZPortfelami() {
        return liczbaUzytkownikowZPortfelami;
    }

    public void setLiczbaUzytkownikowZPortfelami(int liczbaUzytkownikowZPortfelami) {
        this.liczbaUzytkownikowZPortfelami = liczbaUzytkownikowZPortfelami;
    }

    public double getSumaBTC() {
        return sumaBTC;
    }

    public void setSumaBTC(double sumaBTC) {
        this.sumaBTC = sumaBTC;
    }

    public double getSumaETH() {
        return sumaETH;
    }

    public void setSumaETH(double sumaETH) {
        this.sumaETH = sumaETH;
    }
}
