package de.meinshop.webshop.controller;

import de.meinshop.webshop.dao.ArtikelDao;
import de.meinshop.webshop.dao.BestellungDao;
import de.meinshop.webshop.dao.DaoException;
import de.meinshop.webshop.modell.Adresse;
import de.meinshop.webshop.modell.Artikel;
import de.meinshop.webshop.modell.Bestellposition;
import de.meinshop.webshop.modell.Bestellung;
import de.meinshop.webshop.modell.Kunde;
import de.meinshop.webshop.modell.Lastschrift;
import de.meinshop.webshop.modell.Privatkunde;
import de.meinshop.webshop.modell.Rechnungskauf;
import de.meinshop.webshop.modell.Warenkorb;
import de.meinshop.webshop.modell.Zahlungsart;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;

@Named
@RequestScoped
public class KasseController {

    private static final int ZAHLUNGSZIEL_TAGE = 14;

    @Inject
    private LoginController login;

    @Inject
    private WarenkorbController warenkorbController;

    @Inject
    private BestellungDao bestellungDao;

    @Inject
    private ArtikelDao artikelDao;

    @Inject
    private Event<MeldebestandEreignis> meldebestand;

    private int lieferadresseId;
    private int rechnungsadresseId;
    private String zahlungsart = "Rechnungskauf";
    private String iban;
    private String kontoinhaber;

    @PostConstruct
    public void init() {
        Kunde kunde = this.login.getKunde();
        if (kunde != null) {
            if (!kunde.getLieferadressen().isEmpty()) {
                this.lieferadresseId = kunde.getLieferadressen().get(0).getAdresseId();
            }
            if (!kunde.getRechnungsadressen().isEmpty()) {
                this.rechnungsadresseId = kunde.getRechnungsadressen().get(0).getAdresseId();
            }
        }
    }

    public String abschicken() {
        Kunde kunde = this.login.getKunde();
        Warenkorb warenkorb = this.warenkorbController.getWarenkorb();

        if (kunde == null) {
            Meldung.fehler("Bitte zuerst als Kunde anmelden.");
            return null;
        }
        if (warenkorb.istLeer()) {
            Meldung.fehler("Der Warenkorb ist leer.");
            return null;
        }

        if (this.zahlungsart.equals("Rechnungskauf") && kunde instanceof Privatkunde) {
            Privatkunde privat = (Privatkunde) kunde;
            if (!privat.istVolljaehrig()) {
                Meldung.fehler("Kauf auf Rechnung ist erst ab 18 Jahren moeglich.");
                return null;
            }
        }

        Zahlungsart art;
        if (this.zahlungsart.equals("Lastschrift")) {
            if (this.iban == null || this.iban.isBlank()) {
                Meldung.fehler("Bitte IBAN angeben.");
                return null;
            }
            art = new Lastschrift(this.iban.replace(" ", ""), this.kontoinhaber);
        } else {
            art = new Rechnungskauf(ZAHLUNGSZIEL_TAGE);
        }

        Bestellung bestellung = new Bestellung(this.bestellungDao.naechsteBestellnummer(),
                kunde, sucheAdresse(kunde.getLieferadressen(), this.lieferadresseId),
                sucheAdresse(kunde.getRechnungsadressen(), this.rechnungsadresseId), art);
        bestellung.uebernehmeWarenkorb(warenkorb);

        try {
            this.bestellungDao.speichern(bestellung);
        } catch (DaoException e) {
            Meldung.fehler(e.getMessage());
            return null;
        }

        pruefeMeldebestand(bestellung);
        warenkorb.leeren();
        return "danke?faces-redirect=true&nr=" + bestellung.getBestellnummer();
    }

    private void pruefeMeldebestand(Bestellung bestellung) {
        for (Bestellposition position : bestellung.getPositionen()) {
            Artikel artikel = this.artikelDao.findeNachNummer(position.getArtikelnummer());
            if (artikel != null && artikel.istUnterMeldebestand()) {
                this.meldebestand.fire(new MeldebestandEreignis(artikel.getArtikelnummer(),
                        artikel.getBezeichnung(), artikel.getLagerbestand(),
                        artikel.getMeldebestand()));
            }
        }
    }

    private Adresse sucheAdresse(ArrayList<Adresse> adressen, int adresseId) {
        for (Adresse adresse : adressen) {
            if (adresse.getAdresseId() == adresseId) {
                return adresse;
            }
        }
        return adressen.get(0);
    }

    public int getLieferadresseId() {
        return this.lieferadresseId;
    }

    public void setLieferadresseId(int lieferadresseId) {
        this.lieferadresseId = lieferadresseId;
    }

    public int getRechnungsadresseId() {
        return this.rechnungsadresseId;
    }

    public void setRechnungsadresseId(int rechnungsadresseId) {
        this.rechnungsadresseId = rechnungsadresseId;
    }

    public String getZahlungsart() {
        return this.zahlungsart;
    }

    public void setZahlungsart(String zahlungsart) {
        this.zahlungsart = zahlungsart;
    }

    public String getIban() {
        return this.iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getKontoinhaber() {
        return this.kontoinhaber;
    }

    public void setKontoinhaber(String kontoinhaber) {
        this.kontoinhaber = kontoinhaber;
    }
}
