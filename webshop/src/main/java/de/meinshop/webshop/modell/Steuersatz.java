package de.meinshop.webshop.modell;

import java.math.BigDecimal;

public class Steuersatz {

    private int steuerId;
    private String bezeichnung;
    private BigDecimal prozentsatz;

    public Steuersatz(String bezeichnung, BigDecimal prozentsatz) {
        this.bezeichnung = bezeichnung;
        this.prozentsatz = prozentsatz;
    }

    public int getSteuerId() {
        return this.steuerId;
    }

    public void setSteuerId(int steuerId) {
        this.steuerId = steuerId;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public BigDecimal getProzentsatz() {
        return this.prozentsatz;
    }

    public void setProzentsatz(BigDecimal prozentsatz) {
        this.prozentsatz = prozentsatz;
    }
}
