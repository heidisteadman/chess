package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


public class UserServiceTests {
    @Test
    public void registerTest() throws ResponseException {
        UserService.RegisterRequest regUser = new UserService.RegisterRequest("user", "pass", "me@mail.com");
        UserService.register(regUser);
        ArrayList<UserData> userList = UserService.getUsers();
        UserData expUser = new UserData("user", "pass", "me@mail.com");
        ArrayList<UserData> expUserList = new ArrayList<>();
        expUserList.add(expUser);

        Assertions.assertEquals(expUserList, userList);
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();

    }

    @Test
    public void registerThrowsTest() {
        UserService.RegisterRequest badReq = new UserService.RegisterRequest("user", null, "user@mail.edu");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.register(badReq);
        });
        Assertions.assertEquals("Error: bad request", ex.message());
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void loginBasicTest() throws ResponseException {
        UserService.LoginRequest logReq = new UserService.LoginRequest("name", "easyPassword!");
        UserService.RegisterRequest regExist = new UserService.RegisterRequest("name", "easyPassword!", "myemail@email.com");
        UserService.register(regExist);
        UserService.LoginResponse logged = UserService.login(logReq);

        Assertions.assertEquals("name", logged.getLoginUser());
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void badLoginThrowsTest() {
        UserService.LoginRequest badLog = new UserService.LoginRequest("me", null);
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.login(badLog);
        });

        Assertions.assertEquals("Error: bad request", ex.message());
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void loginNoUserTest() {
        UserService.LoginRequest noUserLog = new UserService.LoginRequest("me", "pass");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.login(noUserLog);
        });

        Assertions.assertEquals("Error: User does not exist", ex.message());
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void loginWrongPassTest() throws ResponseException {
        UserService.LoginRequest wrongPassLog = new UserService.LoginRequest("me", "notPass");
        UserService.RegisterRequest rightPass = new UserService.RegisterRequest("me", "pass", "me@mail.org");
        UserService.register(rightPass);

        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.login(wrongPassLog);
        });

        Assertions.assertEquals("Error: Unauthorized", ex.message());
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void logoutTest() throws ResponseException {
        UserService.RegisterRequest exUser = new UserService.RegisterRequest("me", "password", "me@email.org");
        UserService.RegisterResponse reg = UserService.register(exUser);
        String token = reg.authToken();
        AuthData userAuth = AuthDAO.findAuth(token);
        ArrayList<AuthData> expAuths = new ArrayList<>();
        expAuths.add(userAuth);
        Assertions.assertEquals(expAuths, AuthDAO.authTokens);

        UserService.LogoutRequest validLogout = new UserService.LogoutRequest(token);
        UserService.LogoutResponse result = UserService.logout(validLogout);

        Assertions.assertInstanceOf(UserService.LogoutResponse.class, result);
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void logoutNoAuthTest() {
        UserService.LogoutRequest badLog = new UserService.LogoutRequest(null);
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.logout(badLog);
        });

        Assertions.assertEquals("Error: Unauthorized", ex.message());
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }

    @Test
    public void userClearTest() throws ResponseException {
        UserService.RegisterRequest newReg = new UserService.RegisterRequest("name", "pass", "email@email.org");
        UserService.register(newReg);
        UserService.clearUser();
        ArrayList<UserData> noUsers = new ArrayList<>();

        Assertions.assertEquals(noUsers, UserDAO.users);
        GameService.clearGames();
        UserService.clearUser();
        AuthService.clearAuth();
    }
}
