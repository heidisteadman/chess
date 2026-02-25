package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDAO {
    static ArrayList<UserData> users = new ArrayList<>();

    public static void insertUser(UserData u) {
        users.add(u);
        AuthDAO.createAuth(u);
    }

    public static UserData getUser(String username) {
        for (UserData user : users) {
            String un = user.getUser();
            if (Objects.equals(un, username)) {
                return user;
            }
        }
        return null;
    }

    public static void deleteUser(UserData u) throws DataAccessException{
        if (users.contains(u)) {
            users.remove(u);
        } else {
            throw new DataAccessException("500: User not found");
        }
    }

    public static void clearUserDB(){
        users = new ArrayList<>();
    }

    public static ArrayList<GameData> listGames(UserData u) throws DataAccessException {
        return GameDAO.games;
    }


}
