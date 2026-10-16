package de.meinshop.webshop.dao;

import de.meinshop.webshop.modell.Beitrag;
import de.meinshop.webshop.modell.Faq;
import de.meinshop.webshop.modell.Menueeintrag;
import java.util.ArrayList;

public interface InhaltDao {

    ArrayList<Menueeintrag> findeMenue();

    ArrayList<Faq> findeFaqs();

    ArrayList<Beitrag> findeBeitraege();
}
