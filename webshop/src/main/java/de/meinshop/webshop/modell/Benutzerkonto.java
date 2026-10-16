package de.meinshop.webshop.modell;

import de.meinshop.webshop.helper.PasswortHasher;
import java.time.LocalDateTime;

public class Benutzerkonto {

    public static final int STATUS_ADMIN = 0;
    public static final int STATUS_PRIVAT = 1;
    public static final int STATUS_GESCHAEFT = 2;

    private int kontoId;
    private String benutzername;
    private String passwortHash;
    private boolean aktiv;
    private LocalDateTime passwortGeaendertAm;
    private int status;

    public Benutzerkonto(String benutzername, String passwortHash, int status) {
        if (status < STATUS_ADMIN || status > STATUS_GESCHAEFT) {
            throw new IllegalArgumentException("Unbekannter Status: " + status);
        }
        this.benutzername = benutzername;
        this.passwortHash = passwortHash;
        this.status = status;
        this.aktiv = true;
        this.passwortGeaendertAm = LocalDateTime.now();
    }

    public boolean istAdmin() {
        return this.status == STATUS_ADMIN;
    }

    public void sperren() {
        this.aktiv = false;
    }

    public void entsperren() {
        this.aktiv = true;
    }

    public void aenderePasswort(String neuerHash) {
        this.passwortHash = neuerHash;
        this.passwortGeaendertAm = LocalDateTime.now();
    }

    public boolean pruefePasswort(String eingabe) {
        return PasswortHasher.pruefe(eingabe, this.passwortHash);
    }

    public int getKontoId() {
        return this.kontoId;
    }

    public void setKontoId(int kontoId) {
        this.kontoId = kontoId;
    }

    public String getBenutzername() {
        return this.benutzername;
    }

    public String getPasswortHash() {
        return this.passwortHash;
    }

    public boolean isAktiv() {
        return this.aktiv;
    }

    public LocalDateTime getPasswortGeaendertAm() {
        return this.passwortGeaendertAm;
    }

    public void setPasswortGeaendertAm(LocalDateTime passwortGeaendertAm) {
        this.passwortGeaendertAm = passwortGeaendertAm;
    }

    public int getStatus() {
        return this.status;
    }
}
