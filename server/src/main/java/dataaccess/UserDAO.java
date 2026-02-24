package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    static ArrayList<UserData> users = new ArrayList<>();

    public void insertUser(UserData u) throws DataAccessException{
        users.add(u);
        AuthDAO.createAuth(u);
    }

    public UserData getUser(UserData u) throws DataAccessException{
        if (users.contains(u)) {
            return u;
        } else {
            throw new DataAccessException("500: User not found");
        }
    }

    public void deleteUser(UserData u) throws DataAccessException{
        if (users.contains(u)) {
            users.remove(u);
        } else {
            throw new DataAccessException("500: User not found");
        }
    }

    public void clearUserDB(){
        users = new ArrayList<>();
    }

    public List<GameData> listGames(UserData u) throws DataAccessException {
        return GameDAO.games;
    }


}
