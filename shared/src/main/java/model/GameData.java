package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game ) {
    public String getName() {return gameName;}
    public int getID() {return gameID;}
    public String getWhite() {return whiteUsername;}
    public String getBlack() {return blackUsername;}
    public ChessGame getChess() {return game;}
}
