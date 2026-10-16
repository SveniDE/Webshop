package de.meinshop.webshop.modell;

import java.math.BigDecimal;

public class Geschaeftskunde extends Kunde {

    private static final BigDecimal HUNDERT = new BigDecimal("100");

    private String firmenname;
    private String UstId;
    private BigDecimal rabattProzent = BigDecimal.ZERO;

    public Geschaeftskunde(String kundennummer, String vorname, String nachname,
            String email, String firmenname) {
        super(kundennummer, vorname, nachname, email);
        this.firmenname = firmenname;
    }

    @Override
    public BigDecimal getRabattProzent() {
        return this.rabattProzent;
    }

    public void setRabattProzent(BigDecimal rabattProzent) {
        if (rabattProzent.compareTo(BigDecimal.ZERO) < 0
                || rabattProzent.compareTo(HUNDERT) > 0) {
            throw new IllegalArgumentException(
                    "Rabatt muss zwischen 0 und 100 liegen: " + rabattProzent);
        }
        this.rabattProzent = rabattProzent;
    }

    public String getFirmenname() {
        return this.firmenname;
    }

    public void setFirmenname(String firmenname) {
        this.firmenname = firmenname;
    }

    public String getUstId() {
        return this.UstId;
    }

    public void setUstId(String UstId) {
        this.UstId = UstId;
    }
}
