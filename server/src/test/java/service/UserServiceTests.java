package service;

import dataaccess.AuthDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


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
        UserService.clearUser();

    }

    @Test
    public void registerThrowsTest() {
        UserService.RegisterRequest badReq = new UserService.RegisterRequest("user", null, "user@mail.edu");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.register(badReq);
        });
        Assertions.assertEquals("Error: bad request", ex.message());
    }

    @Test
    public void loginBasicTest() throws ResponseException {
        UserService.LoginRequest logReq = new UserService.LoginRequest("name", "easyPassword!");
        UserService.RegisterRequest regExist = new UserService.RegisterRequest("name", "easyPassword!", "myemail@email.com");
        UserService.register(regExist);
        UserService.LoginResponse logged = UserService.login(logReq);

        Assertions.assertEquals("name", logged.getLoginUser());
    }

    @Test
    public void badLoginThrowsTest() {
        UserService.LoginRequest badLog = new UserService.LoginRequest("me", null);
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.login(badLog);
        });

        Assertions.assertEquals("Error: bad request", ex.message());
    }

    @Test
    public void loginNoUserTest() {
        UserService.LoginRequest noUserLog = new UserService.LoginRequest("me", "pass");
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.login(noUserLog);
        });

        Assertions.assertEquals("Error: User does not exist", ex.message());
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

    }

    @Test
    public void logoutNoAuthTest() {
        UserService.LogoutRequest badLog = new UserService.LogoutRequest(null);
        ResponseException ex = Assertions.assertThrows(ResponseException.class, () -> {
            UserService.logout(badLog);
        });

        Assertions.assertEquals("Error: Unauthorized", ex.message());
    }
}
