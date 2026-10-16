package de.meinshop.webshop.controller;

public class MeldebestandEreignis {

    private String artikelnummer;
    private String bezeichnung;
    private int lagerbestand;
    private int meldebestand;

    public MeldebestandEreignis(String artikelnummer, String bezeichnung,
            int lagerbestand, int meldebestand) {
        this.artikelnummer = artikelnummer;
        this.bezeichnung = bezeichnung;
        this.lagerbestand = lagerbestand;
        this.meldebestand = meldebestand;
    }

    public String getArtikelnummer() {
        return this.artikelnummer;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public int getLagerbestand() {
        return this.lagerbestand;
    }

    public int getMeldebestand() {
        return this.meldebestand;
    }
}
