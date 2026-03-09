package service;

import dataaccess.MySQLAuthDAO;
import exception.ResponseException;

public class MySQLAuthService {
    private MySQLAuthDAO authDAO;

    public void clear() throws ResponseException {
        authDAO.clear();
    }
}
