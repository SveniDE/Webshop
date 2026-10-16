package de.meinshop.webshop.controller;

import de.meinshop.webshop.dao.ArtikelDao;
import de.meinshop.webshop.dao.BestellungDao;
import de.meinshop.webshop.dao.DaoException;
import de.meinshop.webshop.modell.Artikel;
import de.meinshop.webshop.modell.Bestellung;
import de.meinshop.webshop.modell.Einheit;
import de.meinshop.webshop.modell.Rechnung;
import de.meinshop.webshop.modell.Steuersatz;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

@Named
@RequestScoped
public class AdminController {

    @Inject
    private LoginController login;

    @Inject
    private BestellungDao bestellungDao;

    @Inject
    private ArtikelDao artikelDao;

    @Inject
    private MeldebestandBeobachter beobachter;

    private ArrayList<Bestellung> bestellungen;
    private ArrayList<Rechnung> rechnungen;
    private ArrayList<Artikel> artikelListe;
    private ArrayList<Steuersatz> steuersaetze;
    private ArrayList<Einheit> einheiten;

    private String neuNummer;
    private String neuBezeichnung;
    private String neuBeschreibung;
    private BigDecimal neuPreis;
    private int neuSteuerId;
    private int neuEinheitId;
    private int neuBestand;
    private int neuMeldebestand;

    @PostConstruct
    public void init() {
        if (!this.login.isAdmin()) {
            return;
        }
        ladeAlles();
    }

    private void ladeAlles() {
        this.bestellungen = this.bestellungDao.findeAlle();
        this.rechnungen = this.bestellungDao.findeRechnungen(this.bestellungen);
        this.artikelListe = this.artikelDao.findeAlle();
        this.steuersaetze = this.artikelDao.findeSteuersaetze();
        this.einheiten = this.artikelDao.findeEinheiten();
    }

    public String pruefeZugriff() {
        if (!this.login.isAdmin()) {
            return "konto?faces-redirect=true";
        }
        return null;
    }

    public void ausfuehren(Bestellung bestellung) {
        if (!this.login.isAdmin()) {
            return;
        }
        try {
            bestellung.ausfuehren(LocalDate.now());
            this.bestellungDao.ausfuehren(bestellung);
            Meldung.info(bestellung.getBestellnummer() + " ist ausgefuehrt.");
        } catch (IllegalStateException | DaoException e) {
            Meldung.fehler(e.getMessage());
        }
        ladeAlles();
    }

    public void stornieren(Bestellung bestellung) {
        if (!this.login.isAdmin()) {
            return;
        }
        try {
            bestellung.stornieren(LocalDate.now());
            this.bestellungDao.stornieren(bestellung);
            Meldung.info(bestellung.getBestellnummer() + " ist storniert, Ware zurueck im Lager.");
        } catch (IllegalStateException | DaoException e) {
            Meldung.fehler(e.getMessage());
        }
        ladeAlles();
    }

    public void rechnungErstellen(Bestellung bestellung) {
        if (!this.login.isAdmin()) {
            return;
        }
        if (rechnungZu(bestellung) != null) {
            Meldung.fehler("Zu " + bestellung.getBestellnummer() + " gibt es schon eine Rechnung.");
            return;
        }
        try {
            Rechnung rechnung = new Rechnung(this.bestellungDao.naechsteRechnungsnummer(),
                    bestellung, LocalDate.now());
            this.bestellungDao.speichereRechnung(rechnung);
            Meldung.info("Rechnung " + rechnung.getRechnungsnummer() + " erstellt.");
        } catch (IllegalArgumentException | DaoException e) {
            Meldung.fehler(e.getMessage());
        }
        ladeAlles();
    }

    public Rechnung rechnungZu(Bestellung bestellung) {
        for (Rechnung rechnung : this.rechnungen) {
            if (rechnung.getBestellung().getBestellungId() == bestellung.getBestellungId()) {
                return rechnung;
            }
        }
        return null;
    }

    public void artikelSpeichern() {
        if (!this.login.isAdmin()) {
            return;
        }
        try {
            for (Artikel artikel : this.artikelListe) {
                this.artikelDao.aktualisieren(artikel);
            }
            Meldung.info("Artikel gespeichert.");
        } catch (DaoException e) {
            Meldung.fehler(e.getMessage());
        }
        ladeAlles();
    }

    public void artikelAnlegen() {
        if (!this.login.isAdmin()) {
            return;
        }
        try {
            Artikel artikel = new Artikel(this.neuNummer, this.neuBezeichnung, this.neuPreis,
                    sucheSteuersatz(this.neuSteuerId), sucheEinheit(this.neuEinheitId));
            artikel.setBeschreibung(this.neuBeschreibung);
            artikel.setLagerbestand(this.neuBestand);
            artikel.setMeldebestand(this.neuMeldebestand);
            this.artikelDao.anlegen(artikel);
            Meldung.info(artikel.getArtikelnummer() + " angelegt.");
        } catch (IllegalArgumentException | DaoException e) {
            Meldung.fehler(e.getMessage());
        }
        ladeAlles();
    }

    private Steuersatz sucheSteuersatz(int steuerId) {
        for (Steuersatz satz : this.steuersaetze) {
            if (satz.getSteuerId() == steuerId) {
                return satz;
            }
        }
        throw new IllegalArgumentException("Bitte einen Steuersatz waehlen.");
    }

    private Einheit sucheEinheit(int einheitId) {
        for (Einheit einheit : this.einheiten) {
            if (einheit.getEinheitId() == einheitId) {
                return einheit;
            }
        }
        throw new IllegalArgumentException("Bitte eine Einheit waehlen.");
    }

    public ArrayList<String> getMeldungen() {
        return this.beobachter.getMeldungen();
    }

    public ArrayList<Bestellung> getBestellungen() {
        return this.bestellungen;
    }

    public ArrayList<Artikel> getArtikelListe() {
        return this.artikelListe;
    }

    public ArrayList<Steuersatz> getSteuersaetze() {
        return this.steuersaetze;
    }

    public ArrayList<Einheit> getEinheiten() {
        return this.einheiten;
    }

    public String getNeuNummer() {
        return this.neuNummer;
    }

    public void setNeuNummer(String neuNummer) {
        this.neuNummer = neuNummer;
    }

    public String getNeuBezeichnung() {
        return this.neuBezeichnung;
    }

    public void setNeuBezeichnung(String neuBezeichnung) {
        this.neuBezeichnung = neuBezeichnung;
    }

    public String getNeuBeschreibung() {
        return this.neuBeschreibung;
    }

    public void setNeuBeschreibung(String neuBeschreibung) {
        this.neuBeschreibung = neuBeschreibung;
    }

    public BigDecimal getNeuPreis() {
        return this.neuPreis;
    }

    public void setNeuPreis(BigDecimal neuPreis) {
        this.neuPreis = neuPreis;
    }

    public int getNeuSteuerId() {
        return this.neuSteuerId;
    }

    public void setNeuSteuerId(int neuSteuerId) {
        this.neuSteuerId = neuSteuerId;
    }

    public int getNeuEinheitId() {
        return this.neuEinheitId;
    }

    public void setNeuEinheitId(int neuEinheitId) {
        this.neuEinheitId = neuEinheitId;
    }

    public int getNeuBestand() {
        return this.neuBestand;
    }

    public void setNeuBestand(int neuBestand) {
        this.neuBestand = neuBestand;
    }

    public int getNeuMeldebestand() {
        return this.neuMeldebestand;
    }

    public void setNeuMeldebestand(int neuMeldebestand) {
        this.neuMeldebestand = neuMeldebestand;
    }
}
