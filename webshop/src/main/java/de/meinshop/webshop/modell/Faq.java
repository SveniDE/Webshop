package de.meinshop.webshop.modell;

public class Faq {

    private int faqId;
    private String frage;
    private String antwort;

    public Faq(String frage, String antwort) {
        this.frage = frage;
        this.antwort = antwort;
    }

    public int getFaqId() {
        return this.faqId;
    }

    public void setFaqId(int faqId) {
        this.faqId = faqId;
    }

    public String getFrage() {
        return this.frage;
    }

    public void setFrage(String frage) {
        this.frage = frage;
    }

    public String getAntwort() {
        return this.antwort;
    }

    public void setAntwort(String antwort) {
        this.antwort = antwort;
    }
}
