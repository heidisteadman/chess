package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class MySQLUserDAO implements SQLUserDAO, SQLDAO{
    public MySQLUserDAO() throws ResponseException {
        SQLDAO.configureDatabase(CREATE_USERS);
    }

    public void clear() throws ResponseException {
        String state = "TRUNCATE users";
        SQLDAO.executeUpdate(state);
    }

    public UserData insertUser(String username, String password, String email) throws ResponseException {
        String state = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashPass = BCrypt.hashpw(password, BCrypt.gensalt());
        SQLDAO.executeUpdate(state, username, hashPass, email);
        return new UserData(username, password, email);

    }

    public void deleteUser(UserData u) throws ResponseException {
        String user = u.getUser();
        String state = "DELETE FROM users WHERE username=?";
        SQLDAO.executeUpdate(state, user);
    }

    public UserData findUser(String username) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException | DataAccessException sqlex) {
            throw new ResponseException(500, "failed to get user from database");
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String user = rs.getString("username");
        String hashpass = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(user, hashpass, email);
    }
}
