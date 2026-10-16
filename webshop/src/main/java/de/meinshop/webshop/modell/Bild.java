package de.meinshop.webshop.modell;

public class Bild {

    private int bildId;
    private String dateiname;
    private String dateiendung;
    private byte[] daten;
    private boolean hauptbild;

    public Bild(String dateiname, String dateiendung, byte[] daten) {
        this.dateiname = dateiname;
        this.dateiendung = dateiendung;
        this.daten = daten;
    }

    public String getVollerDateiname() {
        return this.dateiname + "." + this.dateiendung;
    }

    public String getMimeTyp() {
        if (this.dateiendung.equalsIgnoreCase("jpg")) {
            return "image/jpeg";
        }
        return "image/" + this.dateiendung.toLowerCase();
    }

    public int getBildId() {
        return this.bildId;
    }

    public void setBildId(int bildId) {
        this.bildId = bildId;
    }

    public String getDateiname() {
        return this.dateiname;
    }

    public String getDateiendung() {
        return this.dateiendung;
    }

    public byte[] getDaten() {
        return this.daten;
    }

    public boolean isHauptbild() {
        return this.hauptbild;
    }

    public void setHauptbild(boolean hauptbild) {
        this.hauptbild = hauptbild;
    }
}
