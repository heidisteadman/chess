package service;

import dataaccess.AuthDAO;

public class AuthService {
    public record ClearAuthRequest() {}
    public record ClearAuthResponse() {}

    public ClearAuthResponse clearAuth(ClearAuthRequest c) {
        AuthDAO.clearAuthDB();
        return new ClearAuthResponse();
    }
}
