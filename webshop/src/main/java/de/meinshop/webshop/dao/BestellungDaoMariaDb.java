package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Adresse;
import de.meinshop.webshop.modell.Bestellposition;
import de.meinshop.webshop.modell.Bestellung;
import de.meinshop.webshop.modell.Kunde;
import de.meinshop.webshop.modell.Lastschrift;
import de.meinshop.webshop.modell.Rechnung;
import de.meinshop.webshop.modell.Rechnungskauf;
import de.meinshop.webshop.modell.Zahlungsart;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.sql.DataSource;

@ApplicationScoped
public class BestellungDaoMariaDb implements BestellungDao {

    private static final int ZAHLUNGSZIEL_TAGE = 14;

    @Resource(lookup = "java:app/jdbc/shop")
    private DataSource datenQuelle;

    @Inject
    private KundeDao kundeDao;

    public BestellungDaoMariaDb() {
    }

    public BestellungDaoMariaDb(DataSource datenQuelle, KundeDao kundeDao) {
        this.datenQuelle = datenQuelle;
        this.kundeDao = kundeDao;
    }

    @Override
    public String naechsteBestellnummer() {
        return naechsteNummer("SELECT COUNT(*) FROM tbl_bestellung", "B-");
    }

    @Override
    public String naechsteRechnungsnummer() {
        return naechsteNummer("SELECT COUNT(*) FROM tbl_rechnung", "R-");
    }

    private String naechsteNummer(String sql, String praefix) {
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            ergebnis.next();
            String laufend = "0000" + (ergebnis.getInt(1) + 1);
            laufend = laufend.substring(laufend.length() - 4);
            return praefix + LocalDate.now().getYear() + "-" + laufend;
        } catch (SQLException e) {
            throw new DaoException("Keine neue Nummer moeglich", e);
        }
    }

    @Override
    public void speichern(Bestellung bestellung) {
        String sqlBestand = "UPDATE tbl_artikel SET lagerbestand = lagerbestand - ?"
                + " WHERE artikelnummer = ? AND lagerbestand >= ?";
        String sqlKopf = "INSERT INTO tbl_bestellung (bestellnummer, kunde_id,"
                + " lieferadresse_id, rechnungsadresse_id, bestelldatum, zahlungsart)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        String sqlPosition = "INSERT INTO tbl_bestellposition (bestellung_id,"
                + " artikelnummer, bezeichnung, einzelpreis_netto, steuersatz_prozent, menge)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection verbindung = this.datenQuelle.getConnection()) {
            verbindung.setAutoCommit(false);
            try {
                // 1. Bestand verringern
                try (PreparedStatement anweisung = verbindung.prepareStatement(sqlBestand)) {
                    for (Bestellposition position : bestellung.getPositionen()) {
                        anweisung.setInt(1, position.getMenge());
                        anweisung.setString(2, position.getArtikelnummer());
                        anweisung.setInt(3, position.getMenge());
                        if (anweisung.executeUpdate() == 0) {
                            throw new DaoException("Nicht genug auf Lager: "
                                    + position.getBezeichnung());
                        }
                    }
                }
                // 2. Bestellung speichern
                try (PreparedStatement anweisung = verbindung.prepareStatement(sqlKopf,
                        Statement.RETURN_GENERATED_KEYS)) {
                    anweisung.setString(1, bestellung.getBestellnummer());
                    anweisung.setInt(2, bestellung.getKunde().getKundeId());
                    anweisung.setInt(3, bestellung.getLieferadresse().getAdresseId());
                    anweisung.setInt(4, bestellung.getRechnungsadresse().getAdresseId());
                    anweisung.setObject(5, bestellung.getBestelldatum());
                    anweisung.setString(6, bestellung.getZahlungsart().getBezeichnung());
                    anweisung.executeUpdate();
                    try (ResultSet schluessel = anweisung.getGeneratedKeys()) {
                        schluessel.next();
                        bestellung.setBestellungId(schluessel.getInt(1));
                    }
                }
                // 3. Positionen speichern
                try (PreparedStatement anweisung = verbindung.prepareStatement(sqlPosition)) {
                    for (Bestellposition position : bestellung.getPositionen()) {
                        anweisung.setInt(1, bestellung.getBestellungId());
                        anweisung.setString(2, position.getArtikelnummer());
                        anweisung.setString(3, position.getBezeichnung());
                        anweisung.setBigDecimal(4, position.getEinzelpreisNetto());
                        anweisung.setBigDecimal(5, position.getSteuersatzProzent());
                        anweisung.setInt(6, position.getMenge());
                        anweisung.executeUpdate();
                    }
                }
                verbindung.commit();
            } catch (SQLException | DaoException e) {
                // etwas ist schiefgegangen: alles zuruecknehmen
                verbindung.rollback();
                throw e;
            } finally {
                verbindung.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DaoException("Bestellung konnte nicht gespeichert werden", e);
        }
    }

    @Override
    public ArrayList<Bestellung> findeAlle() {
        String sql = "SELECT * FROM tbl_bestellung ORDER BY bestelldatum DESC";
        ArrayList<Bestellung> liste = new ArrayList<>();
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            while (ergebnis.next()) {
                liste.add(baueBestellung(verbindung, ergebnis));
            }
        } catch (SQLException e) {
            throw new DaoException("Bestellungen nicht lesbar", e);
        }
        return liste;
    }

    @Override
    public void ausfuehren(Bestellung bestellung) {
        String sql = "UPDATE tbl_bestellung SET ausfuehrungsdatum = ? WHERE bestellung_id = ?";
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setObject(1, bestellung.getAusfuehrungsdatum());
            anweisung.setInt(2, bestellung.getBestellungId());
            anweisung.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Bestellung nicht gespeichert", e);
        }
    }

    @Override
    public void stornieren(Bestellung bestellung) {
        String sqlKopf = "UPDATE tbl_bestellung SET ausfuehrungsdatum = ?, storniert = TRUE"
                + " WHERE bestellung_id = ?";
        String sqlBestand = "UPDATE tbl_artikel SET lagerbestand = lagerbestand + ?"
                + " WHERE artikelnummer = ?";
        try (Connection verbindung = this.datenQuelle.getConnection()) {
            verbindung.setAutoCommit(false);
            try (PreparedStatement kopf = verbindung.prepareStatement(sqlKopf);
                 PreparedStatement bestand = verbindung.prepareStatement(sqlBestand)) {
                kopf.setObject(1, bestellung.getAusfuehrungsdatum());
                kopf.setInt(2, bestellung.getBestellungId());
                kopf.executeUpdate();
                for (Bestellposition position : bestellung.getPositionen()) {
                    bestand.setInt(1, position.getMenge());
                    bestand.setString(2, position.getArtikelnummer());
                    bestand.executeUpdate();
                }
                verbindung.commit();
            } catch (SQLException e) {
                verbindung.rollback();
                throw e;
            } finally {
                verbindung.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DaoException("Stornierung nicht gespeichert", e);
        }
    }

    @Override
    public void speichereRechnung(Rechnung rechnung) {
        String sql = "INSERT INTO tbl_rechnung (rechnungsnummer, bestellung_id, rechnungsdatum)"
                + " VALUES (?, ?, ?)";
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            anweisung.setString(1, rechnung.getRechnungsnummer());
            anweisung.setInt(2, rechnung.getBestellung().getBestellungId());
            anweisung.setObject(3, rechnung.getRechnungsdatum());
            anweisung.executeUpdate();
            try (ResultSet schluessel = anweisung.getGeneratedKeys()) {
                if (schluessel.next()) {
                    rechnung.setRechnungId(schluessel.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Rechnung nicht gespeichert", e);
        }
    }

    @Override
    public ArrayList<Rechnung> findeRechnungen(ArrayList<Bestellung> bestellungen) {
        String sql = "SELECT rechnung_id, rechnungsnummer, bestellung_id, rechnungsdatum"
                + " FROM tbl_rechnung";
        ArrayList<Rechnung> liste = new ArrayList<>();
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            while (ergebnis.next()) {
                int bestellungId = ergebnis.getInt("bestellung_id");
                for (Bestellung bestellung : bestellungen) {
                    if (bestellung.getBestellungId() == bestellungId) {
                        Rechnung rechnung = new Rechnung(ergebnis.getString("rechnungsnummer"),
                                bestellung, ergebnis.getObject("rechnungsdatum", LocalDate.class));
                        rechnung.setRechnungId(ergebnis.getInt("rechnung_id"));
                        liste.add(rechnung);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Rechnungen nicht lesbar", e);
        }
        return liste;
    }

    private Bestellung baueBestellung(Connection verbindung, ResultSet zeile)
            throws SQLException {
        Kunde kunde = this.kundeDao.findeKunde(zeile.getInt("kunde_id"));
        Adresse liefer = sucheAdresse(kunde, zeile.getInt("lieferadresse_id"));
        Adresse rechnung = sucheAdresse(kunde, zeile.getInt("rechnungsadresse_id"));

        Bestellung bestellung = new Bestellung(zeile.getString("bestellnummer"), kunde,
                liefer, rechnung, baueZahlungsart(zeile.getString("zahlungsart")));
        bestellung.setBestellungId(zeile.getInt("bestellung_id"));
        bestellung.setBestelldatum(zeile.getObject("bestelldatum", LocalDateTime.class));
        ladePositionen(verbindung, bestellung);

        LocalDate datum = zeile.getObject("ausfuehrungsdatum", LocalDate.class);
        if (datum != null) {
            if (zeile.getBoolean("storniert")) {
                bestellung.stornieren(datum);
            } else {
                bestellung.ausfuehren(datum);
            }
        }
        return bestellung;
    }

    private void ladePositionen(Connection verbindung, Bestellung bestellung)
            throws SQLException {
        String sql = "SELECT * FROM tbl_bestellposition WHERE bestellung_id = ?"
                + " ORDER BY position_id";
        try (PreparedStatement anweisung = verbindung.prepareStatement(sql)) {
            anweisung.setInt(1, bestellung.getBestellungId());
            try (ResultSet ergebnis = anweisung.executeQuery()) {
                while (ergebnis.next()) {
                    Bestellposition position = new Bestellposition(
                            ergebnis.getString("artikelnummer"),
                            ergebnis.getString("bezeichnung"),
                            ergebnis.getBigDecimal("einzelpreis_netto"),
                            ergebnis.getBigDecimal("steuersatz_prozent"),
                            ergebnis.getInt("menge"));
                    position.setPositionId(ergebnis.getInt("position_id"));
                    bestellung.addPosition(position);
                }
            }
        }
    }

    private Zahlungsart baueZahlungsart(String bezeichnung) {
        if (bezeichnung.equals("Lastschrift")) {
            return new Lastschrift(null, null);
        }
        return new Rechnungskauf(ZAHLUNGSZIEL_TAGE);
    }

    private Adresse sucheAdresse(Kunde kunde, int adresseId) {
        ArrayList<Adresse> alle = new ArrayList<>();
        alle.add(kunde.getAnschrift());
        alle.addAll(kunde.getRechnungsadressen());
        alle.addAll(kunde.getLieferadressen());
        for (Adresse adresse : alle) {
            if (adresse != null && adresse.getAdresseId() == adresseId) {
                return adresse;
            }
        }
        throw new DaoException("Adresse " + adresseId + " gehoert nicht zu Kunde "
                + kunde.getKundennummer());
    }
}
