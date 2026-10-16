package de.meinshop.webshop.dao;

public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DaoException(String meldung, Throwable ursache) {
        super(meldung, ursache);
    }

    public DaoException(String meldung) {
        super(meldung);
    }
}
