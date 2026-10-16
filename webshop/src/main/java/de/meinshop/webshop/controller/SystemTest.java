package de.meinshop.webshop.controller;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Named
@RequestScoped
public class SystemTest {

    @Resource(lookup = "java:app/jdbc/shop")
    private DataSource datenQuelle;

    private String status = "noch nicht geprueft";
    private int anzahlArtikel = -1;

    public String getDatenbankStatus() {
        pruefen();
        return this.status;
    }

    public int getAnzahlArtikel() {
        return this.anzahlArtikel;
    }

    private void pruefen() {
        String sql = "SELECT COUNT(*) AS anzahl FROM tbl_artikel";

        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {

            if (ergebnis.next()) {
                this.anzahlArtikel = ergebnis.getInt("anzahl");
                this.status = "Verbindung steht";
            }
        } catch (SQLException e) {
            this.status = "Fehler: " + e.getMessage();
            this.anzahlArtikel = -1;
        }
    }
}
