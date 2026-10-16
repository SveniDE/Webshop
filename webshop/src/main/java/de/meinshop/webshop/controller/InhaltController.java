package de.meinshop.webshop.controller;

import de.meinshop.webshop.dao.InhaltDao;
import de.meinshop.webshop.modell.Beitrag;
import de.meinshop.webshop.modell.Faq;
import de.meinshop.webshop.modell.Menueeintrag;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.ArrayList;

@Named
@RequestScoped
public class InhaltController {

    @Inject
    private InhaltDao inhaltDao;

    private ArrayList<Menueeintrag> menue;
    private ArrayList<Faq> faqs;
    private ArrayList<Beitrag> beitraege;

    @PostConstruct
    public void init() {
        this.menue = this.inhaltDao.findeMenue();
        this.faqs = this.inhaltDao.findeFaqs();
        this.beitraege = this.inhaltDao.findeBeitraege();
    }

    public ArrayList<Menueeintrag> getMenue() {
        return this.menue;
    }

    public ArrayList<Faq> getFaqs() {
        return this.faqs;
    }

    public ArrayList<Beitrag> getBeitraege() {
        return this.beitraege;
    }
}
