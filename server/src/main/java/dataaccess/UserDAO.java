package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    ArrayList<UserData> users = new ArrayList<>();

    public void insertUser(UserData u) throws DataAccessException{}

    public UserData getUser(UserData u) throws DataAccessException{
        if (users.contains(u)) {
            return u;
        } else {
            throw new DataAccessException("User not found");
        }
    }

    public void deleteUser(UserData u) throws DataAccessException{}

    public void clearUserDB(){
        users = new ArrayList<>();
    }

    public List<GameData> listGames(UserData u) throws DataAccessException {}


}
