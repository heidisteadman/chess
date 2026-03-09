package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface SQLUserDAO {
    void clear() throws ResponseException;
    void insertUser(UserData u) throws ResponseException;
    void deleteUser(UserData u) throws ResponseException;
    UserData getUser(String username) throws ResponseException;
}
