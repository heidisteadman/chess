package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Objects;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResponse(String username, String password) {}
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String username, String authToken) {}
    public record LogoutRequest(String authToken) {}
    public record LogoutResponse(String message) {}

    public static RegisterResponse register(RegisterRequest r) throws ResponseException {
        UserData newUser = new UserData(r.username, r.password, r.email);
        UserData findUser = UserDAO.getUser(r.username);
        if (findUser != null) {
            UserDAO.insertUser(newUser);
            return new RegisterResponse(r.username, r.password);
        } else {
            throw new ResponseException(403, "Error: Already Taken");
        }
    }

    public static LoginResponse login(LoginRequest l) throws ResponseException {
        UserData logUser = UserDAO.getUser(l.username);
        if (logUser != null) {
            String pass = l.password;
            String upass = logUser.getPassword();
            if (Objects.equals(pass, upass)) {
                AuthData tokenData = AuthDAO.createAuth(logUser);
                String token = tokenData.getToken();
                return new LoginResponse(l.username, token);
            } else {
                throw new ResponseException(401, "Error: Unauthorized");
            }
        } else {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    public static LogoutResponse logout(LogoutRequest l) throws ResponseException {
        AuthData logAuth = AuthDAO.findAuth(l.authToken);
        if (logAuth != null) {
            UserData logUser = UserDAO.getUser(logAuth.getUser());
            UserDAO.deleteUser(logUser);
            AuthDAO.deleteAuth(logAuth);
            return new LogoutResponse("Logout Success");
        } else {
            throw new ResponseException(401, "Error: Unauthorized");
        }
    }

    public static void clearUser() {
        UserDAO.clearUserDB();
    }
}
