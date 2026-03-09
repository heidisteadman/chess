package service;

import dataaccess.MySQLAuthDAO;
import exception.ResponseException;

public class MySQLAuthService {
    private final MySQLAuthDAO authDAO;

    public MySQLAuthService(MySQLAuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void clear() throws ResponseException {
        authDAO.clear();
    }
}
