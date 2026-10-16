package de.meinshop.webshop;

import de.meinshop.webshop.modell.Adresse;
import de.meinshop.webshop.modell.Artikel;
import de.meinshop.webshop.modell.Bestellung;
import de.meinshop.webshop.modell.Einheit;
import de.meinshop.webshop.modell.Geschaeftskunde;
import de.meinshop.webshop.modell.Lastschrift;
import de.meinshop.webshop.modell.Menueeintrag;
import de.meinshop.webshop.modell.Privatkunde;
import de.meinshop.webshop.modell.Rechnung;
import de.meinshop.webshop.modell.Rechnungskauf;
import de.meinshop.webshop.modell.Steuersatz;
import de.meinshop.webshop.modell.Warenkorb;
import de.meinshop.webshop.modell.Warenkorbposition;
import java.math.BigDecimal;
import java.time.LocalDate;

// Probelauf: legt Objekte aus allen Fachklassen an und gibt die Ergebnisse aus.
// Steht in der Ausgabe "FEHLER", fehlt in der Klasse eine Pruefung.
public class Probelauf {

    public static void main(String[] args) {

        // Teil 1: Artikel anlegen, Bruttopreis und Lagerbestand pruefen
        System.out.println("=== Teil 1: Artikel ===");
        Steuersatz ermaessigt = new Steuersatz("ermaessigt", new BigDecimal("7.00"));
        Steuersatz voll = new Steuersatz("voll", new BigDecimal("19.00"));
        Einheit stueck = new Einheit("Stk", "Stueck");

        Artikel buch = new Artikel("A-1001", "Java lernen", new BigDecimal("30.00"), ermaessigt, stueck);
        Artikel maus = new Artikel("A-1002", "Funkmaus", new BigDecimal("19.90"), voll, stueck);
        Artikel kaffee = new Artikel("A-1003", "Kaffee gemahlen", new BigDecimal("6.50"), ermaessigt, stueck);
        buch.setLagerbestand(12);
        buch.setMeldebestand(3);
        maus.setLagerbestand(4);
        maus.setMeldebestand(5);

        System.out.println(buch.getBezeichnung() + ": netto " + buch.getNettopreis() + ", brutto " + buch.getBruttopreis());
        System.out.println(maus.getBezeichnung() + ": netto " + maus.getNettopreis() + ", brutto " + maus.getBruttopreis());
        System.out.println(kaffee.getBezeichnung() + ": netto " + kaffee.getNettopreis() + ", brutto " + kaffee.getBruttopreis());
        System.out.println("Funkmaus lieferbar: " + maus.istLieferbar());
        System.out.println("Funkmaus unter Meldebestand: " + maus.istUnterMeldebestand());
        System.out.println("Kaffee lieferbar: " + kaffee.istLieferbar());

        // Teil 2: Falsche Werte uebergeben. Die Klasse muss Nein sagen (Exception werfen)
        System.out.println();
        System.out.println("=== Teil 2: Regeln ===");
        try {
            buch.setNettopreis(new BigDecimal("-5.00"));
            System.out.println("FEHLER: negativer Preis wurde angenommen");
        } catch (IllegalArgumentException e) {
            System.out.println("Richtig abgelehnt: " + e.getMessage());
        }
        try {
            buch.verringereBestand(20);
            System.out.println("FEHLER: mehr verkauft als vorhanden");
        } catch (IllegalStateException e) {
            System.out.println("Richtig abgelehnt: " + e.getMessage());
        }
        buch.verringereBestand(10);
        System.out.println("Java lernen nach Verkauf von 10: Bestand " + buch.getLagerbestand()
                + ", unter Meldebestand: " + buch.istUnterMeldebestand());
        try {
            new Menueeintrag("agb.xhtml", "AGB");
            System.out.println("FEHLER: Kurzform mit Punkt wurde angenommen");
        } catch (IllegalArgumentException e) {
            System.out.println("Richtig abgelehnt: " + e.getMessage());
        }
        Menueeintrag service = new Menueeintrag("service", "Service");
        Menueeintrag faq = new Menueeintrag("faq", "Haeufige Fragen");
        service.addUntereintrag(faq);
        System.out.println("Service ist Hauptpunkt: " + service.istHauptpunkt() + ", FAQ ist Hauptpunkt: " + faq.istHauptpunkt()
                + ", Datei: " + faq.getDateiname());

        // Teil 3: Warenkorb. Derselbe Artikel ein zweites Mal erhoeht nur die Menge
        System.out.println();
        System.out.println("=== Teil 3: Warenkorb ===");
        buch.setLagerbestand(12);
        Warenkorb warenkorb = new Warenkorb();
        warenkorb.hinzufuegen(buch, 2);
        warenkorb.hinzufuegen(maus, 1);
        warenkorb.hinzufuegen(buch, 1);
        for (Warenkorbposition position : warenkorb.getPositionen()) {
            System.out.println(position.getMenge() + " x " + position.getArtikel().getBezeichnung()
                    + " = " + position.getSummeBrutto());
        }
        System.out.println("Positionen: " + warenkorb.getPositionen().size() + ", Teile: " + warenkorb.getAnzahlArtikel());
        System.out.println("Summe netto: " + warenkorb.getSummeNetto() + ", Summe brutto: " + warenkorb.getSummeBrutto());
        try {
            warenkorb.hinzufuegen(kaffee, 0);
            System.out.println("FEHLER: Menge 0 wurde angenommen");
        } catch (IllegalArgumentException e) {
            System.out.println("Richtig abgelehnt: " + e.getMessage());
        }

        // Teil 4: Kunde bestellt den Warenkorb. Eine Rechnung gibt es erst nach ausfuehren()
        System.out.println();
        System.out.println("=== Teil 4: Kunde, Bestellung, Rechnung ===");
        Adresse adresse = new Adresse("Hauptstrasse", "12", "57072", "Siegen");
        Privatkunde privat = new Privatkunde("K-1001", "Aylin", "Yildiz", "aylin@example.de", LocalDate.of(1990, 5, 14));
        privat.setAnschrift(adresse);
        privat.addLieferadresse(adresse);
        privat.addRechnungsadresse(adresse);
        System.out.println(privat.getVollerName() + ", volljaehrig: " + privat.istVolljaehrig()
                + ", Rabatt: " + privat.getRabattProzent() + " %");
        System.out.println("Anschrift: " + privat.getAnschrift().getEinzeilig());

        Bestellung bestellung = new Bestellung("B-2026-0001", privat, adresse, adresse, new Rechnungskauf(14));
        bestellung.uebernehmeWarenkorb(warenkorb);
        for (BigDecimal satz : bestellung.getSteuersaetze()) {
            System.out.println("Steuer " + satz + " %: netto " + bestellung.getSummeNettoFuerSteuersatz(satz)
                    + ", Steuer " + bestellung.getSteuerFuerSteuersatz(satz));
        }
        System.out.println("Bestellung netto " + bestellung.getSummeNetto() + ", Steuer " + bestellung.getSummeSteuer()
                + ", brutto " + bestellung.getSummeBrutto() + ", Status " + bestellung.getStatus());

        try {
            new Rechnung("R-2026-0001", bestellung, LocalDate.of(2026, 10, 12));
            System.out.println("FEHLER: Rechnung fuer offene Bestellung erstellt");
        } catch (IllegalArgumentException e) {
            System.out.println("Richtig abgelehnt: " + e.getMessage());
        }
        bestellung.ausfuehren(LocalDate.of(2026, 10, 12));
        System.out.println("Status nach Ausfuehren: " + bestellung.getStatus());
        Rechnung rechnung = new Rechnung("R-2026-0001", bestellung, LocalDate.of(2026, 10, 12));
        System.out.println("Rechnung " + rechnung.getRechnungsnummer() + ": " + rechnung.getRechnungsbetrag()
                + " EUR, faellig am " + rechnung.getFaelligkeit());
        System.out.println(bestellung.getZahlungsart().getZahlungshinweis());
        try {
            bestellung.stornieren(LocalDate.of(2026, 10, 14));
            System.out.println("FEHLER: ausgefuehrte Bestellung wurde storniert");
        } catch (IllegalStateException e) {
            System.out.println("Richtig abgelehnt: " + e.getMessage());
        }

        // Geschaeftskunde: 10 % Rabatt und Zahlung per Lastschrift
        Geschaeftskunde firma = new Geschaeftskunde("K-1002", "Max", "Brandt", "info@brandt.de", "Brandt GmbH");
        firma.setRabattProzent(new BigDecimal("10"));
        Lastschrift lastschrift = new Lastschrift("DE89370400440532013000", "Brandt GmbH");
        Bestellung firmenbestellung = new Bestellung("B-2026-0002", firma, adresse, adresse, lastschrift);
        firmenbestellung.uebernehmeWarenkorb(warenkorb);
        System.out.println(firma.getFirmenname() + " mit " + firma.getRabattProzent() + " % Rabatt: brutto "
                + firmenbestellung.getSummeBrutto());
        System.out.println("Lastschrift von " + lastschrift.getIbanMaskiert() + ", faellig am "
                + lastschrift.berechneFaelligkeit(LocalDate.of(2026, 10, 12)));
    }
}
 