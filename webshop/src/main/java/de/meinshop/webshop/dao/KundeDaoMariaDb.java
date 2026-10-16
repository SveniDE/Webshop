package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Adresse;
import de.meinshop.webshop.modell.Benutzerkonto;
import de.meinshop.webshop.modell.Kunde;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javax.sql.DataSource;

@ApplicationScoped
public class KundeDaoMariaDb implements KundeDao {

    private static final String SQL_KUNDE =
            "SELECT k.*, b.konto_id, b.benutzername, b.passwort_hash, b.aktiv,"
            + " b.passwort_geaendert_am, b.status"
            + " FROM tbl_kunde k JOIN tbl_benutzerkonto b ON b.kunde_id = k.kunde_id";

    @Resource(lookup = "java:app/jdbc/shop")
    private DataSource datenQuelle;

    public KundeDaoMariaDb() {
    }

    public KundeDaoMariaDb(DataSource datenQuelle) {
        this.datenQuelle = datenQuelle;
    }

    @Override
    public Benutzerkonto findeKonto(String benutzername) {
        String sql = "SELECT konto_id, benutzername, passwort_hash, aktiv,"
                + " passwort_geaendert_am, status FROM tbl_benutzerkonto"
                + " WHERE benutzername = ?";
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setString(1, benutzername);
            try (ResultSet ergebnis = anweisung.executeQuery()) {
                if (!ergebnis.next()) {
                    return null;
                }
                return baueKonto(ergebnis);
            }
        } catch (SQLException e) {
            throw new DaoException("Konto " + benutzername + " nicht lesbar", e);
        }
    }

    @Override
    public Kunde findeKundeZuKonto(int kontoId) {
        return findeEinen(SQL_KUNDE + " WHERE b.konto_id = ?", kontoId);
    }

    @Override
    public Kunde findeKunde(int kundeId) {
        return findeEinen(SQL_KUNDE + " WHERE k.kunde_id = ?", kundeId);
    }

    private Kunde findeEinen(String sql, int id) {
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setInt(1, id);
            try (ResultSet ergebnis = anweisung.executeQuery()) {
                if (!ergebnis.next()) {
                    return null;
                }
                Kunde kunde = KundeFactory.erzeuge(ergebnis);
                kunde.setKonto(baueKonto(ergebnis));
                ladeAdressen(verbindung, kunde);
                return kunde;
            }
        } catch (SQLException e) {
            throw new DaoException("Kunde nicht lesbar", e);
        }
    }

    private Benutzerkonto baueKonto(ResultSet ergebnis) throws SQLException {
        Benutzerkonto konto = new Benutzerkonto(ergebnis.getString("benutzername"),
                ergebnis.getString("passwort_hash"), ergebnis.getInt("status"));
        konto.setKontoId(ergebnis.getInt("konto_id"));
        konto.setPasswortGeaendertAm(
                ergebnis.getObject("passwort_geaendert_am", LocalDateTime.class));
        if (!ergebnis.getBoolean("aktiv")) {
            konto.sperren();
        }
        return konto;
    }

    private void ladeAdressen(Connection verbindung, Kunde kunde) throws SQLException {
        String sql = "SELECT adresse_id, art, strasse, hausnummer, plz, ort, land"
                + " FROM tbl_adresse WHERE kunde_id = ? ORDER BY adresse_id";
        try (PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setInt(1, kunde.getKundeId());
            try (ResultSet ergebnis = anweisung.executeQuery()) {
                while (ergebnis.next()) {
                    Adresse adresse = new Adresse(ergebnis.getString("strasse"),
                            ergebnis.getString("hausnummer"), ergebnis.getString("plz"),
                            ergebnis.getString("ort"));
                    adresse.setAdresseId(ergebnis.getInt("adresse_id"));
                    adresse.setLand(ergebnis.getString("land"));
                    String art = ergebnis.getString("art");
                    if (art.equals("ANSCHRIFT")) {
                        kunde.setAnschrift(adresse);
                    } else if (art.equals("RECHNUNG")) {
                        kunde.addRechnungsadresse(adresse);
                    } else {
                        kunde.addLieferadresse(adresse);
                    }
                }
            }
        }
    }
}
