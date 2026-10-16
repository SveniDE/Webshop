package de.meinshop.webshop.modell;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Artikel {

    private static final BigDecimal HUNDERT = new BigDecimal("100");

    private int artikelId;
    private String artikelnummer;
    private String bezeichnung;
    private String beschreibung;
    private BigDecimal nettopreis;
    private int lagerbestand;
    private int meldebestand;
    private String ean;
    private Steuersatz steuersatz;
    private Einheit einheit;
    private ArrayList<Bild> bilder = new ArrayList<>();

    public Artikel(String artikelnummer, String bezeichnung, BigDecimal nettopreis,
            Steuersatz steuersatz, Einheit einheit) {
        this.artikelnummer = artikelnummer;
        this.bezeichnung = bezeichnung;
        pruefePreis(nettopreis);
        this.nettopreis = nettopreis;
        this.steuersatz = steuersatz;
        this.einheit = einheit;
    }

    public BigDecimal getBruttopreis() {
        BigDecimal faktor = BigDecimal.ONE.add(
                this.steuersatz.getProzentsatz().divide(HUNDERT));
        return this.nettopreis.multiply(faktor).setScale(2, RoundingMode.HALF_UP);
    }

    public void addBild(Bild bild) {
        this.bilder.add(bild);
    }

    public ArrayList<Bild> getBilder() {
        return new ArrayList<>(this.bilder);
    }

    public Bild getHauptbild() {
        for (Bild bild : this.bilder) {
            if (bild.isHauptbild()) {
                return bild;
            }
        }
        if (this.bilder.isEmpty()) {
            return null;
        }
        return this.bilder.get(0);
    }

    public boolean istLieferbar() {
        return this.lagerbestand > 0;
    }

    public boolean istUnterMeldebestand() {
        return this.lagerbestand < this.meldebestand;
    }

    public void verringereBestand(int menge) {
        if (menge <= 0) {
            throw new IllegalArgumentException("Menge muss positiv sein: " + menge);
        }
        if (menge > this.lagerbestand) {
            throw new IllegalStateException("Nur noch " + this.lagerbestand
                    + " Stueck von " + this.artikelnummer + " auf Lager");
        }
        this.lagerbestand = this.lagerbestand - menge;
    }

    public int getArtikelId() {
        return this.artikelId;
    }

    public void setArtikelId(int artikelId) {
        this.artikelId = artikelId;
    }

    public String getArtikelnummer() {
        return this.artikelnummer;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBeschreibung() {
        return this.beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public BigDecimal getNettopreis() {
        return this.nettopreis;
    }

    public void setNettopreis(BigDecimal nettopreis) {
        pruefePreis(nettopreis);
        this.nettopreis = nettopreis;
    }

    private static void pruefePreis(BigDecimal preis) {
        if (preis.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preis darf nicht negativ sein: " + preis);
        }
    }

    public int getLagerbestand() {
        return this.lagerbestand;
    }

    public void setLagerbestand(int lagerbestand) {
        if (lagerbestand < 0) {
            throw new IllegalArgumentException("Bestand darf nicht negativ sein");
        }
        this.lagerbestand = lagerbestand;
    }

    public int getMeldebestand() {
        return this.meldebestand;
    }

    public void setMeldebestand(int meldebestand) {
        this.meldebestand = meldebestand;
    }

    public String getEan() {
        return this.ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public Steuersatz getSteuersatz() {
        return this.steuersatz;
    }

    public void setSteuersatz(Steuersatz steuersatz) {
        this.steuersatz = steuersatz;
    }

    public Einheit getEinheit() {
        return this.einheit;
    }

    public void setEinheit(Einheit einheit) {
        this.einheit = einheit;
    }
}
