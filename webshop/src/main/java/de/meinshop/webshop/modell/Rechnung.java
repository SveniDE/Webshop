package de.meinshop.webshop.modell;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Rechnung {

    private int rechnungId;
    private String rechnungsnummer;
    private LocalDate rechnungsdatum;
    private Bestellung bestellung;

    public Rechnung(String rechnungsnummer, Bestellung bestellung, LocalDate rechnungsdatum) {
        if (!bestellung.getStatus().equals(Bestellung.STATUS_AUSGEFUEHRT)) {
            throw new IllegalArgumentException("Rechnung nur fuer ausgefuehrte Bestellungen, "
                    + bestellung.getBestellnummer() + " ist " + bestellung.getStatus());
        }
        this.rechnungsnummer = rechnungsnummer;
        this.bestellung = bestellung;
        this.rechnungsdatum = rechnungsdatum;
    }

    public BigDecimal getRechnungsbetrag() {
        return this.bestellung.getSummeBrutto();
    }

    public LocalDate getFaelligkeit() {
        return this.bestellung.getZahlungsart().berechneFaelligkeit(this.rechnungsdatum);
    }

    public int getRechnungId() {
        return this.rechnungId;
    }

    public void setRechnungId(int rechnungId) {
        this.rechnungId = rechnungId;
    }

    public String getRechnungsnummer() {
        return this.rechnungsnummer;
    }

    public LocalDate getRechnungsdatum() {
        return this.rechnungsdatum;
    }

    public Bestellung getBestellung() {
        return this.bestellung;
    }
}
