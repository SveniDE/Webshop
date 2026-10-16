package de.meinshop.webshop.controller;

import de.meinshop.webshop.dao.ArtikelDao;
import de.meinshop.webshop.modell.Artikel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;

@Named
@RequestScoped
public class ArtikelController {

    @Inject
    private ArtikelDao artikelDao;

    private ArrayList<Artikel> liste;

    private String nummer;
    private Artikel artikel;
    private int menge = 1;

    @PostConstruct
    public void init() {
        this.liste = this.artikelDao.findeAlle();
    }

    public void ladeDetail() {
        if (this.nummer != null) {
            this.artikel = this.artikelDao.findeNachNummer(this.nummer);
        }
        if (this.artikel == null) {
            Meldung.fehler("Diesen Artikel gibt es nicht: " + this.nummer);
        }
    }

    public ArrayList<Artikel> getListe() {
        return this.liste;
    }

    public String getNummer() {
        return this.nummer;
    }

    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    public Artikel getArtikel() {
        return this.artikel;
    }

    public int getMenge() {
        return this.menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }
}
