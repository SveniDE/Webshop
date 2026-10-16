package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Benutzerkonto;
import de.meinshop.webshop.modell.Geschaeftskunde;
import de.meinshop.webshop.modell.Kunde;
import de.meinshop.webshop.modell.Privatkunde;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class KundeFactory {

    private KundeFactory() {
    }

    public static Kunde erzeuge(ResultSet zeile) throws SQLException {
        int status = zeile.getInt("status");
        String kundennummer = zeile.getString("kundennummer");
        String vorname = zeile.getString("vorname");
        String nachname = zeile.getString("nachname");
        String email = zeile.getString("email");

        Kunde kunde;
        if (status == Benutzerkonto.STATUS_PRIVAT) {
            LocalDate geburtsdatum = zeile.getObject("geburtsdatum", LocalDate.class);
            kunde = new Privatkunde(kundennummer, vorname, nachname, email, geburtsdatum);
        } else if (status == Benutzerkonto.STATUS_GESCHAEFT) {
            Geschaeftskunde gk = new Geschaeftskunde(kundennummer, vorname, nachname,
                    email, zeile.getString("firmenname"));
            gk.setUstId(zeile.getString("ustId"));
            BigDecimal rabatt = zeile.getBigDecimal("rabatt_prozent");
            if (rabatt != null) {
                gk.setRabattProzent(rabatt);
            }
            kunde = gk;
        } else {
            throw new DaoException("Status " + status + " gehoert zu keinem Kunden");
        }

        kunde.setKundeId(zeile.getInt("kunde_id"));
        kunde.setAnrede(zeile.getString("anrede"));
        kunde.setTelefon(zeile.getString("telefon"));
        return kunde;
    }
}
