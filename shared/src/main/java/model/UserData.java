package model;

public record UserData(String username, String password, String email) {
    public static String getUser() {
        return username;
    }
}
