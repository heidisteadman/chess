package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;

public class AuthDAO {
    ArrayList<AuthData> authTokens = new ArrayList<>();

    public AuthData createAuth(UserData a) {
        String token = UUID.randomUUID().toString();
        AuthData newToken = new AuthData(UserData.getUser(), token);
        authTokens.add(newToken);
        return newToken;
    }

    public boolean checkAuth(AuthData a) {
        return authTokens.contains(a);
    }

    public void deleteAuth(AuthData a) throws DataAccessException{
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
