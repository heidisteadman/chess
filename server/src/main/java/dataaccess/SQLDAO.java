package dataaccess;

import exception.ResponseException;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public interface SQLDAO {
    String[] CREATE_USERS = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(255) NOT NULL,
            `password` varchar(255) NOT NULL,
            `email` varchar(255) NOT NULL,
            PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    String[] CREATE_AUTH = {
            """
            CREATE TABLE IF NOT EXISTS auths (
            
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    static void configureDatabase(String [] createStatement) throws ResponseException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatement) {
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException sqlex) {
            throw new ResponseException(500, "failed to configure the database");
        }
    }

    static int executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i=0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i+1, p);
                    else if (param instanceof Integer p) ps.setInt(i+1, p);
                    else if (param == null) ps.setNull(i+1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException (500, "Unable to access database");
        }
    }

}
