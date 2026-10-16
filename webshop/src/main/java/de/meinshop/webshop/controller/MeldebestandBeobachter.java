package de.meinshop.webshop.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@ApplicationScoped
public class MeldebestandBeobachter {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private ArrayList<String> meldungen = new ArrayList<>();

    public synchronized void aufMeldebestand(@Observes MeldebestandEreignis ereignis) {
        this.meldungen.add(0, LocalDateTime.now().format(FORMAT) + "  "
                + ereignis.getArtikelnummer() + " " + ereignis.getBezeichnung()
                + ": Bestand " + ereignis.getLagerbestand()
                + ", Meldebestand " + ereignis.getMeldebestand() + ". Bitte nachbestellen.");
    }

    public synchronized ArrayList<String> getMeldungen() {
        return new ArrayList<>(this.meldungen);
    }
}
