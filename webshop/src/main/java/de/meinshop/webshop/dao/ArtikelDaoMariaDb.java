package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Artikel;
import de.meinshop.webshop.modell.Bild;
import de.meinshop.webshop.modell.Einheit;
import de.meinshop.webshop.modell.Steuersatz;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;

@ApplicationScoped
public class ArtikelDaoMariaDb implements ArtikelDao {

    private static final String SQL_ARTIKEL =
            "SELECT a.artikel_id, a.artikelnummer, a.bezeichnung, a.beschreibung,"
            + " a.nettopreis, a.lagerbestand, a.meldebestand, a.ean,"
            + " s.steuer_id, s.bezeichnung AS s_bez, s.prozentsatz,"
            + " e.einheit_id, e.kurz, e.bezeichnung AS e_bez"
            + " FROM tbl_artikel a"
            + " JOIN tbl_steuersatz s ON s.steuer_id = a.steuer_id"
            + " JOIN tbl_einheit e ON e.einheit_id = a.einheit_id";

    @Resource(lookup = "java:app/jdbc/shop")
    private DataSource datenQuelle;

    public ArtikelDaoMariaDb() {
    }

    public ArtikelDaoMariaDb(DataSource datenQuelle) {
        this.datenQuelle = datenQuelle;
    }

    @Override
    public ArrayList<Artikel> findeAlle() {
        String sql = SQL_ARTIKEL + " ORDER BY a.bezeichnung";
        ArrayList<Artikel> liste = new ArrayList<>();
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            while (ergebnis.next()) {
                Artikel artikel = baueArtikel(ergebnis);
                ladeBilder(verbindung, artikel);
                liste.add(artikel);
            }
        } catch (SQLException e) {
            throw new DaoException("Artikel konnten nicht geladen werden", e);
        }
        return liste;
    }

    @Override
    public Artikel findeNachNummer(String artikelnummer) {
        String sql = SQL_ARTIKEL + " WHERE a.artikelnummer = ?";
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setString(1, artikelnummer);
            try (ResultSet ergebnis = anweisung.executeQuery()) {
                if (!ergebnis.next()) {
                    return null;
                }
                Artikel artikel = baueArtikel(ergebnis);
                ladeBilder(verbindung, artikel);
                return artikel;
            }
        } catch (SQLException e) {
            throw new DaoException("Artikel " + artikelnummer + " nicht lesbar", e);
        }
    }

    @Override
    public Bild findeBild(int bildId) {
        String sql = "SELECT bild_id, dateiname, dateiendung, daten, ist_hauptbild"
                + " FROM tbl_bild WHERE bild_id = ?";
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setInt(1, bildId);
            try (ResultSet ergebnis = anweisung.executeQuery()) {
                if (!ergebnis.next()) {
                    return null;
                }
                return baueBild(ergebnis);
            }
        } catch (SQLException e) {
            throw new DaoException("Bild " + bildId + " nicht lesbar", e);
        }
    }

    @Override
    public ArrayList<Steuersatz> findeSteuersaetze() {
        String sql = "SELECT steuer_id, bezeichnung, prozentsatz FROM tbl_steuersatz"
                + " ORDER BY prozentsatz";
        ArrayList<Steuersatz> liste = new ArrayList<>();
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            while (ergebnis.next()) {
                Steuersatz satz = new Steuersatz(ergebnis.getString("bezeichnung"),
                        ergebnis.getBigDecimal("prozentsatz"));
                satz.setSteuerId(ergebnis.getInt("steuer_id"));
                liste.add(satz);
            }
        } catch (SQLException e) {
            throw new DaoException("Steuersaetze nicht lesbar", e);
        }
        return liste;
    }

    @Override
    public ArrayList<Einheit> findeEinheiten() {
        String sql = "SELECT einheit_id, kurz, bezeichnung FROM tbl_einheit ORDER BY kurz";
        ArrayList<Einheit> liste = new ArrayList<>();
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            while (ergebnis.next()) {
                Einheit einheit = new Einheit(ergebnis.getString("kurz"),
                        ergebnis.getString("bezeichnung"));
                einheit.setEinheitId(ergebnis.getInt("einheit_id"));
                liste.add(einheit);
            }
        } catch (SQLException e) {
            throw new DaoException("Einheiten nicht lesbar", e);
        }
        return liste;
    }

    @Override
    public void anlegen(Artikel artikel) {
        String sql = "INSERT INTO tbl_artikel (artikelnummer, bezeichnung, beschreibung,"
                + " nettopreis, lagerbestand, meldebestand, ean, steuer_id, einheit_id)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            anweisung.setString(1, artikel.getArtikelnummer());
            anweisung.setString(2, artikel.getBezeichnung());
            anweisung.setString(3, artikel.getBeschreibung());
            anweisung.setBigDecimal(4, artikel.getNettopreis());
            anweisung.setInt(5, artikel.getLagerbestand());
            anweisung.setInt(6, artikel.getMeldebestand());
            anweisung.setString(7, artikel.getEan());
            anweisung.setInt(8, artikel.getSteuersatz().getSteuerId());
            anweisung.setInt(9, artikel.getEinheit().getEinheitId());
            anweisung.executeUpdate();
            try (ResultSet schluessel = anweisung.getGeneratedKeys()) {
                if (schluessel.next()) {
                    artikel.setArtikelId(schluessel.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Artikel " + artikel.getArtikelnummer()
                    + " konnte nicht angelegt werden", e);
        }
    }

    @Override
    public void aktualisieren(Artikel artikel) {
        String sql = "UPDATE tbl_artikel SET bezeichnung = ?, beschreibung = ?,"
                + " nettopreis = ?, lagerbestand = ?, meldebestand = ?, ean = ?,"
                + " steuer_id = ?, einheit_id = ? WHERE artikel_id = ?";
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setString(1, artikel.getBezeichnung());
            anweisung.setString(2, artikel.getBeschreibung());
            anweisung.setBigDecimal(3, artikel.getNettopreis());
            anweisung.setInt(4, artikel.getLagerbestand());
            anweisung.setInt(5, artikel.getMeldebestand());
            anweisung.setString(6, artikel.getEan());
            anweisung.setInt(7, artikel.getSteuersatz().getSteuerId());
            anweisung.setInt(8, artikel.getEinheit().getEinheitId());
            anweisung.setInt(9, artikel.getArtikelId());
            anweisung.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Artikel " + artikel.getArtikelnummer()
                    + " konnte nicht gespeichert werden", e);
        }
    }

    private Artikel baueArtikel(ResultSet ergebnis) throws SQLException {
        Steuersatz satz = new Steuersatz(ergebnis.getString("s_bez"),
                ergebnis.getBigDecimal("prozentsatz"));
        satz.setSteuerId(ergebnis.getInt("steuer_id"));
        Einheit einheit = new Einheit(ergebnis.getString("kurz"), ergebnis.getString("e_bez"));
        einheit.setEinheitId(ergebnis.getInt("einheit_id"));

        Artikel artikel = new Artikel(ergebnis.getString("artikelnummer"),
                ergebnis.getString("bezeichnung"), ergebnis.getBigDecimal("nettopreis"),
                satz, einheit);
        artikel.setArtikelId(ergebnis.getInt("artikel_id"));
        artikel.setBeschreibung(ergebnis.getString("beschreibung"));
        artikel.setLagerbestand(ergebnis.getInt("lagerbestand"));
        artikel.setMeldebestand(ergebnis.getInt("meldebestand"));
        artikel.setEan(ergebnis.getString("ean"));
        return artikel;
    }

    private void ladeBilder(Connection verbindung, Artikel artikel) throws SQLException {
        String sql = "SELECT bild_id, dateiname, dateiendung, daten, ist_hauptbild"
                + " FROM tbl_bild WHERE artikel_id = ? ORDER BY bild_id";
        try (PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setInt(1, artikel.getArtikelId());
            try (ResultSet ergebnis = anweisung.executeQuery()) {
                while (ergebnis.next()) {
                    artikel.addBild(baueBild(ergebnis));
                }
            }
        }
    }

    private Bild baueBild(ResultSet ergebnis) throws SQLException {
        Bild bild = new Bild(ergebnis.getString("dateiname"),
                ergebnis.getString("dateiendung"), ergebnis.getBytes("daten"));
        bild.setBildId(ergebnis.getInt("bild_id"));
        bild.setHauptbild(ergebnis.getBoolean("ist_hauptbild"));
        return bild;
    }
}
