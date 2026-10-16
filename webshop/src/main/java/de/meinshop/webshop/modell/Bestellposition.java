package de.meinshop.webshop.modell;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Bestellposition {

    private static final BigDecimal HUNDERT = new BigDecimal("100");

    private int positionId;
    private String artikelnummer;
    private String bezeichnung;
    private BigDecimal einzelpreisNetto;
    private BigDecimal steuersatzProzent;
    private int menge;

    public Bestellposition(Artikel artikel, int menge, BigDecimal rabattProzent) {
        if (menge <= 0) {
            throw new IllegalArgumentException("Menge muss positiv sein: " + menge);
        }
        BigDecimal anteil = HUNDERT.subtract(rabattProzent).divide(HUNDERT);
        this.artikelnummer = artikel.getArtikelnummer();
        this.bezeichnung = artikel.getBezeichnung();
        this.einzelpreisNetto = artikel.getNettopreis().multiply(anteil)
                .setScale(2, RoundingMode.HALF_UP);
        this.steuersatzProzent = artikel.getSteuersatz().getProzentsatz();
        this.menge = menge;
    }

    public Bestellposition(String artikelnummer, String bezeichnung,
            BigDecimal einzelpreisNetto, BigDecimal steuersatzProzent, int menge) {
        this.artikelnummer = artikelnummer;
        this.bezeichnung = bezeichnung;
        this.einzelpreisNetto = einzelpreisNetto;
        this.steuersatzProzent = steuersatzProzent;
        this.menge = menge;
    }

    public BigDecimal getSummeNetto() {
        return this.einzelpreisNetto.multiply(BigDecimal.valueOf(this.menge));
    }

    public int getPositionId() {
        return this.positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public String getArtikelnummer() {
        return this.artikelnummer;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public BigDecimal getEinzelpreisNetto() {
        return this.einzelpreisNetto;
    }

    public BigDecimal getSteuersatzProzent() {
        return this.steuersatzProzent;
    }

    public int getMenge() {
        return this.menge;
    }
}
