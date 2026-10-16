package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Bestellung;
import de.meinshop.webshop.modell.Rechnung;
import java.util.ArrayList;

public interface BestellungDao {

    String naechsteBestellnummer();

    void speichern(Bestellung bestellung);

    ArrayList<Bestellung> findeAlle();

    void ausfuehren(Bestellung bestellung);

    void stornieren(Bestellung bestellung);

    String naechsteRechnungsnummer();

    void speichereRechnung(Rechnung rechnung);

    ArrayList<Rechnung> findeRechnungen(ArrayList<Bestellung> bestellungen);
}
