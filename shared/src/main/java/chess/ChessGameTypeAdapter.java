package chess;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Objects;

public class ChessGameTypeAdapter implements JsonDeserializer<ChessGame> {
    @Override
    public ChessGame deserialize(JsonElement element, Type type, JsonDeserializationContext json) throws JsonParseException {
        String turn = element.getAsJsonObject().get("teamTurn").getAsString();
        ChessBoard chessBoard = new ChessBoard();
        ChessGame.TeamColor teamTurn;
        if (Objects.equals(turn, "WHITE")) {
            teamTurn = ChessGame.TeamColor.WHITE;
        } else {
            teamTurn = ChessGame.TeamColor.BLACK;
        }
        JsonObject board = element.getAsJsonObject().get("gameBoard").getAsJsonObject();
        JsonArray array = board.get("squares").getAsJsonArray();
        for (int row = 0; row < array.size(); row++) {
            JsonArray pieceRow = array.get(row).getAsJsonArray();
            for (int col = 0; col < pieceRow.size(); col++) {
                if (!pieceRow.get(col).isJsonObject()) {
                    continue;
                }
                JsonObject piece = pieceRow.get(col).getAsJsonObject();
                String color = piece.get("pieceColor").getAsString();
                String pType = piece.get("type").getAsString();
                ChessGame.TeamColor pColor;
                if (color.equals("WHITE")) {
                    pColor = ChessGame.TeamColor.WHITE;
                } else {
                    pColor = ChessGame.TeamColor.BLACK;
                }
                ChessPiece.PieceType pieceType = getPieceType(pType);
                ChessPosition pos = new ChessPosition(row + 1, col + 1);
                chessBoard.addPiece(pos, new ChessPiece(pColor, pieceType));
            }
        }
        boolean end = element.getAsJsonObject().get("ended").getAsBoolean();
        ChessGame game = new ChessGame();
        game.setBoard(chessBoard);
        game.setTeamTurn(teamTurn);
        if (end) {
            game.setEnded();
        }
        return game;
    }

    private static ChessPiece.PieceType getPieceType(String typeStr) {
        ChessPiece.PieceType type = null;
        switch (typeStr) {
            case "KING" -> type = ChessPiece.PieceType.KING;
            case "QUEEN" -> type = ChessPiece.PieceType.QUEEN;
            case "BISHOP" -> type = ChessPiece.PieceType.BISHOP;
            case "KNIGHT" -> type = ChessPiece.PieceType.KNIGHT;
            case "ROOK" -> type = ChessPiece.PieceType.ROOK;
            case "PAWN" -> type = ChessPiece.PieceType.PAWN;
        }
        return type;
    }
}
