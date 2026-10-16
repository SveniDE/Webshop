package de.meinshop.webshop.modell;

import java.time.LocalDate;

public class Rechnungskauf implements Zahlungsart {

    private int zahlungszielTage;

    public Rechnungskauf(int zahlungszielTage) {
        this.zahlungszielTage = zahlungszielTage;
    }

    @Override
    public String getBezeichnung() {
        return "Rechnungskauf";
    }

    @Override
    public LocalDate berechneFaelligkeit(LocalDate rechnungsdatum) {
        return rechnungsdatum.plusDays(this.zahlungszielTage);
    }

    @Override
    public String getZahlungshinweis() {
        return "Zahlbar innerhalb von " + this.zahlungszielTage + " Tagen ohne Abzug.";
    }

    public int getZahlungszielTage() {
        return this.zahlungszielTage;
    }
}
