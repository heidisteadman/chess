package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class UserDAO {
    public static ArrayList<UserData> users = new ArrayList<>();

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

    public static void deleteUser(UserData u) {
        users.remove(u);
    }

    public static void clearUserDB(){
        users = new ArrayList<>();
    }

    public static ArrayList<GameData> listGames() {
        return GameDAO.games;
    }


}
