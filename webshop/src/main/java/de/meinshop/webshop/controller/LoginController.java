package de.meinshop.webshop.controller;

import de.meinshop.webshop.dao.KundeDao;
import de.meinshop.webshop.modell.Benutzerkonto;
import de.meinshop.webshop.modell.Kunde;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
@SuppressWarnings("serial")
public class LoginController implements Serializable {

    @Inject
    private KundeDao kundeDao;

    private String benutzername;
    private String passwort;

    private Benutzerkonto konto;
    private Kunde kunde;

    public String anmelden() {
        Benutzerkonto gefunden = this.kundeDao.findeKonto(this.benutzername);
        if (gefunden == null || !gefunden.pruefePasswort(this.passwort)) {
            Meldung.fehler("Benutzername oder Passwort falsch.");
            return null;
        }
        if (!gefunden.isAktiv()) {
            Meldung.fehler("Dieses Konto ist gesperrt.");
            return null;
        }
        this.konto = gefunden;
        this.passwort = null;
        if (gefunden.istAdmin()) {
            return "admin?faces-redirect=true";
        }
        this.kunde = this.kundeDao.findeKundeZuKonto(gefunden.getKontoId());
        return "konto?faces-redirect=true";
    }

    public String abmelden() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "start?faces-redirect=true";
    }

    public boolean isAngemeldet() {
        return this.konto != null;
    }

    public boolean isAdmin() {
        return this.konto != null && this.konto.istAdmin();
    }

    public boolean isKundeAngemeldet() {
        return this.kunde != null;
    }

    public String getKundenart() {
        if (this.konto == null) {
            return "";
        }
        if (this.konto.getStatus() == Benutzerkonto.STATUS_GESCHAEFT) {
            return "Geschaeftskunde";
        }
        if (this.konto.getStatus() == Benutzerkonto.STATUS_PRIVAT) {
            return "Privatkunde";
        }
        return "Administrator";
    }

    public String getBenutzername() {
        return this.benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getPasswort() {
        return this.passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public Benutzerkonto getKonto() {
        return this.konto;
    }

    public Kunde getKunde() {
        return this.kunde;
    }
}
