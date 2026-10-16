package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Benutzerkonto;
import de.meinshop.webshop.modell.Kunde;

public interface KundeDao {

    Benutzerkonto findeKonto(String benutzername);

    Kunde findeKundeZuKonto(int kontoId);

    Kunde findeKunde(int kundeId);
}
