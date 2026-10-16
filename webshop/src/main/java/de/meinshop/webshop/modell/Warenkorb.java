package de.meinshop.webshop.modell;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Warenkorb {

    private ArrayList<Warenkorbposition> positionen = new ArrayList<>();

    public void hinzufuegen(Artikel artikel, int menge) {
        Warenkorbposition vorhanden = suche(artikel.getArtikelnummer());
        if (vorhanden != null) {
            vorhanden.erhoeheMenge(menge);
        } else {
            this.positionen.add(new Warenkorbposition(artikel, menge));
        }
    }

    public void entfernen(String artikelnummer) {
        Warenkorbposition vorhanden = suche(artikelnummer);
        if (vorhanden != null) {
            this.positionen.remove(vorhanden);
        }
    }

    private Warenkorbposition suche(String artikelnummer) {
        for (Warenkorbposition position : this.positionen) {
            if (position.getArtikel().getArtikelnummer().equals(artikelnummer)) {
                return position;
            }
        }
        return null;
    }

    public BigDecimal getSummeNetto() {
        BigDecimal summe = BigDecimal.ZERO;
        for (Warenkorbposition position : this.positionen) {
            summe = summe.add(position.getSummeNetto());
        }
        return summe;
    }

    public BigDecimal getSummeBrutto() {
        BigDecimal summe = BigDecimal.ZERO;
        for (Warenkorbposition position : this.positionen) {
            summe = summe.add(position.getSummeBrutto());
        }
        return summe;
    }

    public int getAnzahlArtikel() {
        int anzahl = 0;
        for (Warenkorbposition position : this.positionen) {
            anzahl = anzahl + position.getMenge();
        }
        return anzahl;
    }

    public boolean istLeer() {
        return this.positionen.isEmpty();
    }

    public void leeren() {
        this.positionen.clear();
    }

    public ArrayList<Warenkorbposition> getPositionen() {
        return new ArrayList<>(this.positionen);
    }
}
