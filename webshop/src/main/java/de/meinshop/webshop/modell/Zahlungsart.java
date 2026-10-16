package de.meinshop.webshop.modell;

import java.time.LocalDate;

public interface Zahlungsart {

    String getBezeichnung();

    LocalDate berechneFaelligkeit(LocalDate rechnungsdatum);

    String getZahlungshinweis();
}
