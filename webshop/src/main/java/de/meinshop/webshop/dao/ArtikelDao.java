package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Artikel;
import de.meinshop.webshop.modell.Bild;
import de.meinshop.webshop.modell.Einheit;
import de.meinshop.webshop.modell.Steuersatz;
import java.util.ArrayList;

public interface ArtikelDao {

    ArrayList<Artikel> findeAlle();

    Artikel findeNachNummer(String artikelnummer);

    Bild findeBild(int bildId);

    ArrayList<Steuersatz> findeSteuersaetze();

    ArrayList<Einheit> findeEinheiten();

    void anlegen(Artikel artikel);

    void aktualisieren(Artikel artikel);
}
