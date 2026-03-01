package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResponse(String username, String authToken) {}
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String username, String authToken) {
        public String getLoginUser() {
            return username;
        }
    }
    public record LogoutRequest(String authToken) {}
    public record LogoutResponse() {}

    public static ArrayList<UserData> getUsers() {
        return UserDAO.users;
    }

    public static RegisterResponse register(RegisterRequest r) throws ResponseException {
        if ((r.username == null) || (r.password == null) || (r.email == null)) {
            throw new ResponseException(400, "Error: bad request");
        }
        UserData newUser = new UserData(r.username, r.password, r.email);
        UserData findUser = UserDAO.getUser(r.username);
        if (findUser == null) {
            UserDAO.insertUser(newUser);
            String token = AuthDAO.findAuthUser(r.username);
            return new RegisterResponse(r.username, token);
        } else {
            throw new ResponseException(403, "Error: Already Taken");
        }
    }

    public static LoginResponse login(LoginRequest l) throws ResponseException {
        if ((l.username == null) || (l.password == null)) {
            throw new ResponseException(400, "Error: bad request");
        }
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
            throw new ResponseException(401, "Error: User does not exist");
            }
        }

    public static LogoutResponse logout(LogoutRequest l) throws ResponseException {
        AuthData logAuth = AuthDAO.findAuth(l.authToken);
        if (logAuth != null) {
            UserData logUser = UserDAO.getUser(logAuth.getUser());
            UserDAO.deleteUser(logUser);
            AuthDAO.deleteAuth(logAuth);
            return new LogoutResponse();
        } else {
            throw new ResponseException(401, "Error: Unauthorized");
        }
    }

    public static void clearUser() {
        UserDAO.clearUserDB();
    }
}
