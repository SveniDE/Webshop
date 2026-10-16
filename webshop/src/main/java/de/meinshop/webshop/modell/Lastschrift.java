package de.meinshop.webshop.modell;

import java.time.LocalDate;

public class Lastschrift implements Zahlungsart {

    private static final int VORLAUF_TAGE = 2;

    private String iban;
    private String kontoinhaber;

    public Lastschrift(String iban, String kontoinhaber) {
        this.iban = iban;
        this.kontoinhaber = kontoinhaber;
    }

    @Override
    public String getBezeichnung() {
        return "Lastschrift";
    }

    @Override
    public LocalDate berechneFaelligkeit(LocalDate rechnungsdatum) {
        return rechnungsdatum.plusDays(VORLAUF_TAGE);
    }

    @Override
    public String getZahlungshinweis() {
        return "Der Betrag wird per SEPA-Lastschrift von Ihrem Konto eingezogen.";
    }

    public String getIbanMaskiert() {
        if (this.iban == null || this.iban.length() < 4) {
            return "****";
        }
        return "**** " + this.iban.substring(this.iban.length() - 4);
    }

    public String getIban() {
        return this.iban;
    }

    public String getKontoinhaber() {
        return this.kontoinhaber;
    }
}
