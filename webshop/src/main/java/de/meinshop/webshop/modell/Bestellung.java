package de.meinshop.webshop.modell;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Bestellung {

    public static final String STATUS_OFFEN = "offen";
    public static final String STATUS_AUSGEFUEHRT = "ausgefuehrt";
    public static final String STATUS_STORNIERT = "storniert";

    private static final BigDecimal HUNDERT = new BigDecimal("100");

    private int bestellungId;
    private String bestellnummer;
    private Kunde kunde;
    private Adresse lieferadresse;
    private Adresse rechnungsadresse;
    private Zahlungsart zahlungsart;
    private LocalDateTime bestelldatum;
    private LocalDate ausfuehrungsdatum;
    private boolean storniert;
    private ArrayList<Bestellposition> positionen = new ArrayList<>();

    public Bestellung(String bestellnummer, Kunde kunde, Adresse lieferadresse,
            Adresse rechnungsadresse, Zahlungsart zahlungsart) {
        this.bestellnummer = bestellnummer;
        this.kunde = kunde;
        this.lieferadresse = lieferadresse;
        this.rechnungsadresse = rechnungsadresse;
        this.zahlungsart = zahlungsart;
        this.bestelldatum = LocalDateTime.now();
    }

    public void addPosition(Bestellposition position) {
        this.positionen.add(position);
    }

    public void uebernehmeWarenkorb(Warenkorb warenkorb) {
        BigDecimal rabatt = this.kunde.getRabattProzent();
        for (Warenkorbposition wp : warenkorb.getPositionen()) {
            addPosition(new Bestellposition(wp.getArtikel(), wp.getMenge(), rabatt));
        }
    }

    public BigDecimal getSummeNetto() {
        BigDecimal summe = BigDecimal.ZERO;
        for (Bestellposition position : this.positionen) {
            summe = summe.add(position.getSummeNetto());
        }
        return summe;
    }

    public ArrayList<BigDecimal> getSteuersaetze() {
        ArrayList<BigDecimal> saetze = new ArrayList<>();
        for (Bestellposition position : this.positionen) {
            boolean schonDa = false;
            for (BigDecimal satz : saetze) {
                if (satz.compareTo(position.getSteuersatzProzent()) == 0) {
                    schonDa = true;
                }
            }
            if (!schonDa) {
                saetze.add(position.getSteuersatzProzent());
            }
        }
        return saetze;
    }

    public BigDecimal getSummeNettoFuerSteuersatz(BigDecimal satz) {
        BigDecimal summe = BigDecimal.ZERO;
        for (Bestellposition position : this.positionen) {
            if (position.getSteuersatzProzent().compareTo(satz) == 0) {
                summe = summe.add(position.getSummeNetto());
            }
        }
        return summe;
    }

    public BigDecimal getSteuerFuerSteuersatz(BigDecimal satz) {
        return getSummeNettoFuerSteuersatz(satz).multiply(satz).divide(HUNDERT)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSummeSteuer() {
        BigDecimal summe = BigDecimal.ZERO;
        for (BigDecimal satz : getSteuersaetze()) {
            summe = summe.add(getSteuerFuerSteuersatz(satz));
        }
        return summe;
    }

    public BigDecimal getSummeBrutto() {
        return getSummeNetto().add(getSummeSteuer());
    }

    public String getStatus() {
        if (this.ausfuehrungsdatum == null) {
            return STATUS_OFFEN;
        }
        if (this.storniert) {
            return STATUS_STORNIERT;
        }
        return STATUS_AUSGEFUEHRT;
    }

    public void ausfuehren(LocalDate datum) {
        pruefeOffen();
        this.ausfuehrungsdatum = datum;
    }

    public void stornieren(LocalDate datum) {
        pruefeOffen();
        this.ausfuehrungsdatum = datum;
        this.storniert = true;
    }

    private void pruefeOffen() {
        if (!getStatus().equals(STATUS_OFFEN)) {
            throw new IllegalStateException("Bestellung " + this.bestellnummer
                    + " ist bereits " + getStatus());
        }
    }

    public int getBestellungId() {
        return this.bestellungId;
    }

    public void setBestellungId(int bestellungId) {
        this.bestellungId = bestellungId;
    }

    public String getBestellnummer() {
        return this.bestellnummer;
    }

    public Kunde getKunde() {
        return this.kunde;
    }

    public Adresse getLieferadresse() {
        return this.lieferadresse;
    }

    public Adresse getRechnungsadresse() {
        return this.rechnungsadresse;
    }

    public Zahlungsart getZahlungsart() {
        return this.zahlungsart;
    }

    public LocalDateTime getBestelldatum() {
        return this.bestelldatum;
    }

    public void setBestelldatum(LocalDateTime bestelldatum) {
        this.bestelldatum = bestelldatum;
    }

    public LocalDate getAusfuehrungsdatum() {
        return this.ausfuehrungsdatum;
    }

    public boolean isStorniert() {
        return this.storniert;
    }

    public ArrayList<Bestellposition> getPositionen() {
        return new ArrayList<>(this.positionen);
    }
}
