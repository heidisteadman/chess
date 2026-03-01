package service;

import dataaccess.AuthDAO;
import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AuthServiceTests {

    @Test
    public void clearAuthTest() throws ResponseException {
        UserService.RegisterRequest reg = new UserService.RegisterRequest("me", "pass", "me@mail.org");
        UserService.register(reg);
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();

        ArrayList<AuthData> expAuth = new ArrayList<>();

        Assertions.assertEquals(expAuth, AuthDAO.authTokens);
    }
}
