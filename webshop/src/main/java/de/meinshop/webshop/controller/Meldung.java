package de.meinshop.webshop.controller;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class Meldung {

    private Meldung() {
    }

    public static void info(String text) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, text, null));
    }

    public static void fehler(String text) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, text, null));
    }
}
