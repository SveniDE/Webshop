package de.meinshop.webshop.modell;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Privatkunde extends Kunde {

    private LocalDate geburtsdatum;

    public Privatkunde(String kundennummer, String vorname, String nachname,
            String email, LocalDate geburtsdatum) {
        super(kundennummer, vorname, nachname, email);
        this.geburtsdatum = geburtsdatum;
    }

    @Override
    public BigDecimal getRabattProzent() {
        return BigDecimal.ZERO;
    }

    public boolean istVolljaehrig() {
        return istVolljaehrigAm(LocalDate.now());
    }

    public boolean istVolljaehrigAm(LocalDate stichtag) {
        if (this.geburtsdatum == null) {
            return false;
        }
        LocalDate achtzehnterGeburtstag = this.geburtsdatum.plusYears(18);
        return !achtzehnterGeburtstag.isAfter(stichtag);
    }

    public LocalDate getGeburtsdatum() {
        return this.geburtsdatum;
    }

    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }
}
