package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class AuthDAO {
    public static ArrayList<AuthData> authTokens = new ArrayList<>();

    public static AuthData createAuth(UserData a) {
        String token = UUID.randomUUID().toString();
        String user = a.getUser();
        AuthData newToken = new AuthData(token, user);
        authTokens.add(newToken);
        return newToken;
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

    public static String findAuthUser(String username) {
        for (AuthData auth : authTokens) {
            String aUser = auth.getUser();
            if (Objects.equals(aUser, username)) {
                return auth.getToken();
            }
        }
        return null;
    }

    public static void deleteAuth(AuthData a) {
        authTokens.remove(a);
    }

    public static void clearAuthDB(){
        authTokens = new ArrayList<>();
    }
}
