package model;

public record UserData(String username, String password, String email) {
    public String getUser() {
        return username;
    }
}
