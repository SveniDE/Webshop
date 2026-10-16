package de.meinshop.webshop.modell;

import java.util.ArrayList;

public class Menueeintrag {

    private static final int KURZFORM_MAX = 10;

    private int menueId;
    private String kurzform;
    private String bezeichnung;
    private int reihenfolge;
    private Menueeintrag uebergeordnet;
    private ArrayList<Menueeintrag> untereintraege = new ArrayList<>();

    public Menueeintrag(String kurzform, String bezeichnung) {
        pruefeKurzform(kurzform);
        this.kurzform = kurzform;
        this.bezeichnung = bezeichnung;
    }

    public void setKurzform(String kurzform) {
        pruefeKurzform(kurzform);
        this.kurzform = kurzform;
    }

    private static void pruefeKurzform(String kurzform) {
        if (kurzform == null || kurzform.isEmpty() || kurzform.length() > KURZFORM_MAX) {
            throw new IllegalArgumentException(
                    "Kurzform muss 1 bis " + KURZFORM_MAX + " Zeichen haben: " + kurzform);
        }
        if (kurzform.contains(".")) {
            throw new IllegalArgumentException("Kurzform ohne Dateiendung angeben: " + kurzform);
        }
    }

    public void addUntereintrag(Menueeintrag eintrag) {
        eintrag.uebergeordnet = this;
        this.untereintraege.add(eintrag);
    }

    public boolean istHauptpunkt() {
        return this.uebergeordnet == null;
    }

    public String getDateiname() {
        return this.kurzform + ".xhtml";
    }

    public int getMenueId() {
        return this.menueId;
    }

    public void setMenueId(int menueId) {
        this.menueId = menueId;
    }

    public String getKurzform() {
        return this.kurzform;
    }

    public String getBezeichnung() {
        return this.bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public int getReihenfolge() {
        return this.reihenfolge;
    }

    public void setReihenfolge(int reihenfolge) {
        this.reihenfolge = reihenfolge;
    }

    public Menueeintrag getUebergeordnet() {
        return this.uebergeordnet;
    }

    public ArrayList<Menueeintrag> getUntereintraege() {
        return new ArrayList<>(this.untereintraege);
    }
}
