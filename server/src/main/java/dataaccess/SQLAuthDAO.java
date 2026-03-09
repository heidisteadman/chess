package dataaccess;

import exception.ResponseException;
import model.*;

public interface SQLAuthDAO {
    AuthData createAuth(UserData a) throws ResponseException;
    AuthData findAuth(String token) throws ResponseException;
    String findAuthUser(String username) throws ResponseException;
    void clear() throws ResponseException;
    void deleteAuth(AuthData a) throws ResponseException;
}
