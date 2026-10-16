package de.meinshop.webshop.modell;

import java.time.LocalDateTime;

public class Beitrag {

    private int beitragId;
    private String ueberschrift;
    private String einleitung;
    private String haupttext;
    private LocalDateTime erstelltAm;

    public Beitrag(String ueberschrift, String einleitung, String haupttext) {
        this.ueberschrift = ueberschrift;
        this.einleitung = einleitung;
        this.haupttext = haupttext;
        this.erstelltAm = LocalDateTime.now();
    }

    public int getBeitragId() {
        return this.beitragId;
    }

    public void setBeitragId(int beitragId) {
        this.beitragId = beitragId;
    }

    public String getUeberschrift() {
        return this.ueberschrift;
    }

    public void setUeberschrift(String ueberschrift) {
        this.ueberschrift = ueberschrift;
    }

    public String getEinleitung() {
        return this.einleitung;
    }

    public void setEinleitung(String einleitung) {
        this.einleitung = einleitung;
    }

    public String getHaupttext() {
        return this.haupttext;
    }

    public void setHaupttext(String haupttext) {
        this.haupttext = haupttext;
    }

    public LocalDateTime getErstelltAm() {
        return this.erstelltAm;
    }

    public void setErstelltAm(LocalDateTime erstelltAm) {
        this.erstelltAm = erstelltAm;
    }
}
