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
            `username` varchar(255) NOT NULL,
            `authToken` varchar(255) NOT NULL,
            PRIMARY KEY (`authToken`),
            INDEX (`authToken`),
            INDEX (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    String[] CREATE_GAMES = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` INT NOT NULL AUTO_INCREMENT,
            `whiteUser` varchar(255),
            `blackUser` varchar(255),
            `gameName` varchar(255),
            `game` TEXT NOT NULL,
            PRIMARY KEY (`gameID`),
            INDEX (`gameName`)
            ) AUTO_INCREMENT=1000 ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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
            throw new ResponseException(500, "Error: failed to configure the database");
        }
    }

    static int executeUpdate(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, "Error: Unable to access database");
        }
    }

    private static void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            switch (params[i]) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> {}
            }
        }
    }

}
