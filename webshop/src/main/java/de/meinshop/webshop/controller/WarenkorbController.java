package de.meinshop.webshop.controller;

import de.meinshop.webshop.dao.ArtikelDao;
import de.meinshop.webshop.modell.Artikel;
import de.meinshop.webshop.modell.Warenkorb;
import de.meinshop.webshop.modell.Warenkorbposition;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
@SuppressWarnings("serial")
public class WarenkorbController implements Serializable {

    @Inject
    private ArtikelDao artikelDao;

    private Warenkorb warenkorb = new Warenkorb();

    public void hinzufuegenNachNummer(String artikelnummer, int menge) {
        Artikel artikel = this.artikelDao.findeNachNummer(artikelnummer);
        if (artikel == null) {
            Meldung.fehler("Diesen Artikel gibt es nicht: " + artikelnummer);
            return;
        }
        hinzufuegen(artikel, menge);
    }

    public void hinzufuegen(Artikel artikel, int menge) {
        if (!artikel.istLieferbar()) {
            Meldung.fehler(artikel.getBezeichnung() + " ist zurzeit nicht lieferbar.");
            return;
        }
        try {
            this.warenkorb.hinzufuegen(artikel, menge);
            Meldung.info(menge + " x " + artikel.getBezeichnung() + " im Warenkorb.");
        } catch (IllegalArgumentException e) {
            Meldung.fehler(e.getMessage());
        }
    }

    public void erhoehen(Warenkorbposition position) {
        position.erhoeheMenge(1);
    }

    public void verringern(Warenkorbposition position) {
        if (position.getMenge() == 1) {
            entfernen(position);
        } else {
            position.aendereMenge(position.getMenge() - 1);
        }
    }

    public void entfernen(Warenkorbposition position) {
        this.warenkorb.entfernen(position.getArtikel().getArtikelnummer());
    }

    public void leeren() {
        this.warenkorb.leeren();
    }

    public Warenkorb getWarenkorb() {
        return this.warenkorb;
    }
}
