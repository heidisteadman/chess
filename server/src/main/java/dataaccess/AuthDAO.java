package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class AuthDAO {
    static ArrayList<AuthData> authTokens = new ArrayList<>();

    public static AuthData createAuth(UserData a) {
        String token = UUID.randomUUID().toString();
        String user = a.getUser();
        AuthData newToken = new AuthData(token, user);
        authTokens.add(newToken);
        return newToken;
    }

    public boolean checkAuth(AuthData a) {
        return authTokens.contains(a);
    }

    public static AuthData findAuth(String token) {
        for (AuthData auth : authTokens) {
            String aToken = auth.getToken();
            if (Objects.equals(aToken, token)) {
                return auth;
            }
        }
        return null;
    }

    public static void deleteAuth(AuthData a) throws DataAccessException{
        if (authTokens.contains(a)) {
            authTokens.remove(a);
        } else {
            throw new DataAccessException("AuthToken does not exist");
        }
    }

    public void clearAuthDB(){
        authTokens = new ArrayList<>();
    }
}
