package dataaccess;

import exception.ResponseException;
import model.*;
import java.sql.*;

import java.util.UUID;

public class MySQLAuthDAO implements SQLAuthDAO, SQLDAO{
    public MySQLAuthDAO() throws ResponseException {
        SQLDAO.configureDatabase(CREATE_AUTH);
    }

    public AuthData createAuth(UserData u) throws ResponseException {
        String token = UUID.randomUUID().toString();
        String user = u.getUser();
        AuthData newToken = new AuthData(token, user);
        String state = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        SQLDAO.executeUpdate(state, user, token);
        return newToken;
    }

    public AuthData findAuth(String token) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String state = "SELECT username, authToken FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(state)) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException sqex) {
            throw new ResponseException(500, "Error: failed to find auth data");
        }
    }

    public AuthData findAuthUser(String username) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String state = "SELECT username, authToken FROM auths WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(state)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException sqlex) {
            throw new ResponseException(500, "Error: failed to find auth data");
        }
    }

    public void deleteAuth(AuthData a) throws ResponseException {
        String token = a.getToken();
        String state = "DELETE FROM auths WHERE authToken=?";
        SQLDAO.executeUpdate(state, token);
    }

    public void clear() throws ResponseException {
        String state = "TRUNCATE auths";
        SQLDAO.executeUpdate(state);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String user = rs.getString("username");
        String token = rs.getString("authToken");
        return new AuthData(token, user);
    }
}
