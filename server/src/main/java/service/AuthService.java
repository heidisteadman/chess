package service;

import dataaccess.AuthDAO;

public class AuthService {
    public static void clearAuth() {
        AuthDAO.clearAuthDB();
    }
}
