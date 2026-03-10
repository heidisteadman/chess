package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySQLUserDAOTests {
    private MySQLUserDAO userDAO;

    @BeforeEach
    public void makeDAO() throws ResponseException {
        userDAO = new MySQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void insertUserTest() throws ResponseException {
        UserData testUser = new UserData("myName", "myPassword", "myemail");
        userDAO.insertUser(testUser);

        UserData insertedUser = userDAO.getUser(testUser.username());
        Assertions.assertNotNull(insertedUser, "user should exist");
        Assertions.assertEquals(insertedUser.getUser(), testUser.getUser());
    }

    @Test
    public void insertUserDuplicateTest() throws ResponseException {
        UserData test1 = new UserData("myName", "myPassword", "myemail");
        UserData test2 = new UserData("myName", "alsoMyPassword", "alsoMyEmail");
        userDAO.insertUser(test1);

        Assertions.assertThrows(ResponseException.class, ()-> userDAO.insertUser(test2));
    }

    @Test
    public void getUserTest() throws ResponseException {
        UserData testUser = new UserData("myName", "myPassword", "myemail");
        userDAO.insertUser(testUser);

        UserData gotUser = userDAO.getUser(testUser.username());
        Assertions.assertNotNull(gotUser, "user should exist");
        Assertions.assertEquals(testUser.getUser(), gotUser.getUser());
    }

    @Test
    public void getUserNonexistentTest() throws ResponseException {
        UserData gotUser = userDAO.getUser("user");
        Assertions.assertNull(gotUser);
    }

    @Test
    public void clearTest() throws ResponseException {
        UserData test1 = new UserData("myName", "myPassword", "myemail");
        UserData test2 = new UserData("myName2", "alsoMyPassword", "alsoMyEmail");
        userDAO.insertUser(test1);
        userDAO.insertUser(test2);
        userDAO.clear();

        UserData gotUser = userDAO.getUser(test1.username());
        Assertions.assertNull(gotUser);
    }
}
