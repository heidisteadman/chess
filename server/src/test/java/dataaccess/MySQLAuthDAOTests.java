package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySQLAuthDAOTests {
    MySQLAuthDAO authDAO;
    UserData testUser;

    @BeforeEach
    public void setUp() throws ResponseException {
        authDAO = new MySQLAuthDAO();
        authDAO.clear();
        testUser = new UserData("user", "pass", "email@email");
    }

    @Test
    public void createAuthTest() throws ResponseException {
        AuthData testAuth = authDAO.createAuth(testUser);

        Assertions.assertNotNull(testAuth);
        Assertions.assertEquals(testUser.username(), testAuth.getUser());
        Assertions.assertNotNull(testAuth.getToken());
    }

    @Test
    public void createAuthNoUserTest() {
        Assertions.assertThrows(ResponseException.class, () -> authDAO.createAuth(null));
    }

    @Test
    public void findAuthTest() throws ResponseException {
        AuthData testAuth = authDAO.createAuth(testUser);
        String token = testAuth.getToken();
        AuthData foundUser = authDAO.findAuth(token);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(testAuth, foundUser);
    }

    @Test
    public void findAuthNoUserTest() throws ResponseException {
        AuthData foundUser = authDAO.findAuth("test token");

        Assertions.assertNull(foundUser);
    }

    @Test
    public void findAuthUserTest() throws ResponseException {
        AuthData testAuth = authDAO.createAuth(testUser);
        String user = testAuth.getUser();
        AuthData foundUser = authDAO.findAuthUser(user);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(testAuth, foundUser);
    }

    @Test
    public void findAuthUserNoUserTest() throws ResponseException {
        AuthData foundUser = authDAO.findAuthUser("test username");

        Assertions.assertNull(foundUser);
    }

    @Test
    public void deleteAuthTest() throws ResponseException {
        AuthData testAuth = authDAO.createAuth(testUser);
        String token = testAuth.authToken();
        authDAO.deleteAuth(testAuth);
        AuthData foundAuth = authDAO.findAuth(token);

        Assertions.assertNull(foundAuth);
    }

    @Test
    public void clearTest() throws ResponseException {
        AuthData testAuth = authDAO.createAuth(testUser);
        String token = testAuth.getToken();
        authDAO.clear();
        AuthData foundAuth = authDAO.findAuth(token);

        Assertions.assertNull(foundAuth);
    }
}
