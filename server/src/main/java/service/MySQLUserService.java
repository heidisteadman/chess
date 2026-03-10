package service;

import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLUserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class MySQLUserService {
    public MySQLUserService(MySQLUserDAO userDAO, MySQLAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResponse(String username, String authToken) {}
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String username, String authToken) {}
    public record LogoutRequest(String authToken) {}
    public record LogoutResponse() {}

    private final MySQLUserDAO userDAO;
    private final MySQLAuthDAO authDAO;

    public RegisterResponse register(RegisterRequest r) throws ResponseException {
        if ((r.username == null) || (r.password == null) || (r.email == null)) {
            throw new ResponseException(400, "Error: bad request");
        }
        UserData newUser = new UserData(r.username, r.password, r.email);
        UserData findUser = userDAO.getUser(r.username);
        if (findUser == null) {
            userDAO.insertUser(newUser);
            authDAO.createAuth(newUser);
            AuthData authd = authDAO.findAuthUser(r.username);
            if (authd == null) {
                throw new ResponseException(401, "Error: unauthorized register request");
            }
            String token = authd.getToken();
            return new MySQLUserService.RegisterResponse(r.username, token);
        } else {
            throw new ResponseException(403, "Error: Already Taken");
        }
    }

    public LoginResponse login(LoginRequest l) throws ResponseException {
        if ((l.username == null) || (l.password == null)) {
            throw new ResponseException(400, "Error: bad request");
        }
        UserData logUser = userDAO.getUser(l.username);
        if (logUser != null) {
            String upass = l.password;
            String hashpass = logUser.getPassword();

            if (BCrypt.checkpw(upass, hashpass)) {
                AuthData tokenData = authDAO.createAuth(logUser);
                String token = tokenData.getToken();
                return new MySQLUserService.LoginResponse(l.username, token);
            } else {
                throw new ResponseException(401, "Error: Unauthorized login request");
            }
        } else {
            throw new ResponseException(401, "Error: User does not exist");
        }
    }

    public LogoutResponse logout(LogoutRequest l) throws ResponseException {
        AuthData logAuth = authDAO.findAuth(l.authToken);
        if (logAuth != null) {
            authDAO.deleteAuth(logAuth);
            return new MySQLUserService.LogoutResponse();
        } else {
            throw new ResponseException(401, "Error: Unauthorized logout request");
        }
    }

    public void clear() throws ResponseException {
        userDAO.clear();
    }

}
