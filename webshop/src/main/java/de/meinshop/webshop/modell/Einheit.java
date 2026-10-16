package de.meinshop.webshop.modell;

public class Einheit {

    private int einheitId;
    private String kurz;
    private String bezeichnung;

    public Einheit(String kurz, String bezeichnung) {
        this.kurz = kurz;
        this.bezeichnung = bezeichnung;
    }

    public int getEinheitId() {
        return this.einheitId;
    }

    public void setEinheitId(int einheitId) {
        this.einheitId = einheitId;
    }

    public String getKurz() {
        return this.kurz;
    }

    public void setKurz(String kurz) {
        this.kurz = kurz;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }
}
