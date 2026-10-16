package de.meinshop.webshop.controller;

import de.meinshop.webshop.dao.ArtikelDao;
import de.meinshop.webshop.modell.Bild;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("serial")
@WebServlet("/bild")
public class BildServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private ArtikelDao artikelDao;

    @Override
    protected void doGet(HttpServletRequest anfrage, HttpServletResponse antwort)
            throws IOException {
        int id;
        try {
            id = Integer.parseInt(anfrage.getParameter("id"));
        } catch (NumberFormatException e) {
            antwort.sendError(HttpServletResponse.SC_BAD_REQUEST, "id fehlt");
            return;
        }
        Bild bild = this.artikelDao.findeBild(id);
        if (bild == null) {
            antwort.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        antwort.setContentType(bild.getMimeTyp());
        antwort.setContentLength(bild.getDaten().length);
        antwort.getOutputStream().write(bild.getDaten());
    }
}
