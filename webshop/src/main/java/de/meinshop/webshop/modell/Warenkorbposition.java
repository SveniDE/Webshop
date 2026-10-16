package de.meinshop.webshop.modell;

import java.math.BigDecimal;

public class Warenkorbposition {

    private Artikel artikel;
    private int menge;

    public Warenkorbposition(Artikel artikel, int menge) {
        pruefeMenge(menge);
        this.artikel = artikel;
        this.menge = menge;
    }

    public void erhoeheMenge(int anzahl) {
        aendereMenge(this.menge + anzahl);
    }

    public void aendereMenge(int neueMenge) {
        pruefeMenge(neueMenge);
        this.menge = neueMenge;
    }

    private static void pruefeMenge(int menge) {
        if (menge <= 0) {
            throw new IllegalArgumentException("Menge muss positiv sein: " + menge);
        }
    }

    public BigDecimal getSummeNetto() {
        return this.artikel.getNettopreis().multiply(BigDecimal.valueOf(this.menge));
    }

    public BigDecimal getSummeBrutto() {
        return this.artikel.getBruttopreis().multiply(BigDecimal.valueOf(this.menge));
    }

    public Artikel getArtikel() {
        return this.artikel;
    }

    public int getMenge() {
        return this.menge;
    }
}
