package de.meinshop.webshop.modell;

import java.math.BigDecimal;
import java.util.ArrayList;

public abstract class Kunde {

    private int kundeId;
    private String kundennummer;
    private String anrede;
    private String vorname;
    private String nachname;
    private String email;
    private String telefon;
    private Adresse anschrift;
    private ArrayList<Adresse> rechnungsadressen = new ArrayList<>();
    private ArrayList<Adresse> lieferadressen = new ArrayList<>();
    private Benutzerkonto konto;

    public Kunde(String kundennummer, String vorname, String nachname, String email) {
        this.kundennummer = kundennummer;
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
    }

    public abstract BigDecimal getRabattProzent();

    public String getVollerName() {
        return this.vorname + " " + this.nachname;
    }

    public void addRechnungsadresse(Adresse adresse) {
        this.rechnungsadressen.add(adresse);
    }

    public void addLieferadresse(Adresse adresse) {
        this.lieferadressen.add(adresse);
    }

    public ArrayList<Adresse> getRechnungsadressen() {
        return new ArrayList<>(this.rechnungsadressen);
    }

    public ArrayList<Adresse> getLieferadressen() {
        return new ArrayList<>(this.lieferadressen);
    }

    public int getKundeId() {
        return this.kundeId;
    }

    public void setKundeId(int kundeId) {
        this.kundeId = kundeId;
    }

    public String getKundennummer() {
        return this.kundennummer;
    }

    public String getAnrede() {
        return this.anrede;
    }

    public void setAnrede(String anrede) {
        this.anrede = anrede;
    }

    public String getVorname() {
        return this.vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return this.nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return this.telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public Adresse getAnschrift() {
        return this.anschrift;
    }

    public void setAnschrift(Adresse anschrift) {
        this.anschrift = anschrift;
    }

    public Benutzerkonto getKonto() {
        return this.konto;
    }

    public void setKonto(Benutzerkonto konto) {
        this.konto = konto;
    }
}
