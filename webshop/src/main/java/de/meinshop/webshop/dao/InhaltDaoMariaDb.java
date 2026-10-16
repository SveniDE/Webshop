package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Beitrag;
import de.meinshop.webshop.modell.Faq;
import de.meinshop.webshop.modell.Menueeintrag;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.sql.DataSource;

@ApplicationScoped
public class InhaltDaoMariaDb implements InhaltDao {

    @Resource(lookup = "java:app/jdbc/shop")
    private DataSource datenQuelle;

    public InhaltDaoMariaDb() {
    }

    public InhaltDaoMariaDb(DataSource datenQuelle) {
        this.datenQuelle = datenQuelle;
    }

    @Override
    public ArrayList<Menueeintrag> findeMenue() {
        String sql = "SELECT menue_id, kurzform, bezeichnung, eltern_id, reihenfolge"
                + " FROM tbl_menueeintrag ORDER BY reihenfolge";
        ArrayList<Menueeintrag> alle = new ArrayList<>();
        ArrayList<Integer> elternIds = new ArrayList<>();
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            while (ergebnis.next()) {
                Menueeintrag eintrag = new Menueeintrag(ergebnis.getString("kurzform"),
                        ergebnis.getString("bezeichnung"));
                eintrag.setMenueId(ergebnis.getInt("menue_id"));
                eintrag.setReihenfolge(ergebnis.getInt("reihenfolge"));
                alle.add(eintrag);
                int elternId = ergebnis.getInt("eltern_id");
                if (ergebnis.wasNull()) {
                    elternId = 0;
                }
                elternIds.add(elternId);
            }
        } catch (SQLException e) {
            throw new DaoException("Menue nicht lesbar", e);
        }

        ArrayList<Menueeintrag> hauptpunkte = new ArrayList<>();
        for (int i = 0; i < alle.size(); i++) {
            Menueeintrag eintrag = alle.get(i);
            int elternId = elternIds.get(i);
            if (elternId == 0) {
                hauptpunkte.add(eintrag);
            } else {
                for (Menueeintrag moeglicherVater : alle) {
                    if (moeglicherVater.getMenueId() == elternId) {
                        moeglicherVater.addUntereintrag(eintrag);
                    }
                }
            }
        }
        return hauptpunkte;
    }

    @Override
    public ArrayList<Faq> findeFaqs() {
        String sql = "SELECT faq_id, frage, antwort FROM tbl_faq ORDER BY faq_id";
        ArrayList<Faq> liste = new ArrayList<>();
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            while (ergebnis.next()) {
                Faq faq = new Faq(ergebnis.getString("frage"), ergebnis.getString("antwort"));
                faq.setFaqId(ergebnis.getInt("faq_id"));
                liste.add(faq);
            }
        } catch (SQLException e) {
            throw new DaoException("FAQ nicht lesbar", e);
        }
        return liste;
    }

    @Override
    public ArrayList<Beitrag> findeBeitraege() {
        String sql = "SELECT beitrag_id, ueberschrift, einleitung, haupttext, erstellt_am"
                + " FROM tbl_beitrag ORDER BY erstellt_am, beitrag_id";
        ArrayList<Beitrag> liste = new ArrayList<>();
        try (Connection verbindung = this.datenQuelle.getConnection();
             PreparedStatement anweisung = verbindung.prepareStatement(sql);
             ResultSet ergebnis = anweisung.executeQuery()) {
            while (ergebnis.next()) {
                Beitrag beitrag = new Beitrag(ergebnis.getString("ueberschrift"),
                        ergebnis.getString("einleitung"), ergebnis.getString("haupttext"));
                beitrag.setBeitragId(ergebnis.getInt("beitrag_id"));
                beitrag.setErstelltAm(ergebnis.getObject("erstellt_am", LocalDateTime.class));
                liste.add(beitrag);
            }
        } catch (SQLException e) {
            throw new DaoException("Beitraege nicht lesbar", e);
        }
        return liste;
    }
}
