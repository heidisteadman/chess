package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;


public class WebsocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException{
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            String auth = command.getAuthToken();
            MySQLAuthDAO authDAO = new MySQLAuthDAO();
            AuthData authData = authDAO.findAuth(auth);
            String user = authData.getUser();
            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand conCommand = new Gson().fromJson(ctx.message(), ConnectCommand.class);
                    connect(ctx.session, conCommand, user);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(ctx.session, moveCommand, user);
                }
                case LEAVE -> {
                    LeaveCommand leaveCommand = new Gson().fromJson(ctx.message(), LeaveCommand.class);
                    leave(ctx.session, leaveCommand, user);
                }
                case RESIGN -> {
                    ResignCommand resignCommand = new Gson().fromJson(ctx.message(), ResignCommand.class);
                    resign(ctx.session, resignCommand, user);
                }
            }
        } catch (ResponseException ex) {
            sendMessage(ctx.session.getRemote(), new ServerMessage(ServerMessage.ServerMessageType.ERROR));
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void sendMessage(RemoteEndpoint remote, ServerMessage message) throws IOException {
        remote.sendString(message.toString());
    }

    private void connect(Session session, ConnectCommand connectCommand, String username) throws ResponseException, IOException{
        connections.add(session);
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData game = gameDAO.getGame(connectCommand.getGameID());
        if (game == null) {
            ErrorMessage error = new ErrorMessage("Error: Game ID is not associated with existing game.");
            sendMessage(session.getRemote(), error);
            return;
        }
        String gameJson = new Gson().toJson(game.getChess());
        LoadGameMessage loadGame = new LoadGameMessage(gameJson);
        sendMessage(session.getRemote(), loadGame);
        String message;
        if (Objects.equals(username, game.whiteUsername())) {
            message = String.format("%s joined as WHITE", username);
        } else if (Objects.equals(username, game.blackUsername())) {
            message = String.format("%s joined as BLACK", username);
        } else {
            message = String.format("%s joined as an observer.", username);
        }
        NotificationMessage notificationMessage = new NotificationMessage(message);
        connections.broadcast(session, notificationMessage);

    }

    public void makeMove(Session session, MakeMoveCommand moveCommand, String username) throws ResponseException, IOException{
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData game = gameDAO.getGame(moveCommand.getGameID());
        if (game == null) {
            ErrorMessage error = new ErrorMessage("Error: game ID is not associated with an existing game.");
            sendMessage(session.getRemote(), error);
            return;
        }
        ErrorMessage error = getErrorMessage(username, game);
        if (error != null) {
            sendMessage(session.getRemote(), error);
            return;
        }
        if ((Objects.equals(username, game.whiteUsername())) && (game.getChess().getTeamTurn() == ChessGame.TeamColor.WHITE) && (isStalemate(moveCommand.getGameID(), ChessGame.TeamColor.WHITE)!=null)) {
            NotificationMessage stale = new NotificationMessage(isStalemate(moveCommand.getGameID(), ChessGame.TeamColor.WHITE));
            connections.broadcast(session, stale);
            sendMessage(session.getRemote(), stale);
            gameDAO.endGame(moveCommand.getGameID());
            LoadGameMessage staleLoad = new LoadGameMessage(new Gson().toJson(game.getChess()));
            connections.broadcast(session, staleLoad);
            sendMessage(session.getRemote(), staleLoad);
        } else if ((Objects.equals(username, game.blackUsername())) && (game.getChess().getTeamTurn() == ChessGame.TeamColor.BLACK) && (isStalemate(moveCommand.getGameID(), ChessGame.TeamColor.BLACK)!=null)) {
            NotificationMessage stale = new NotificationMessage(isStalemate(moveCommand.getGameID(), ChessGame.TeamColor.WHITE));
            connections.broadcast(session, stale);
            sendMessage(session.getRemote(), stale);
            gameDAO.endGame(moveCommand.getGameID());
            LoadGameMessage staleLoad = new LoadGameMessage(new Gson().toJson(game.getChess()));
            connections.broadcast(session, staleLoad);
            sendMessage(session.getRemote(), staleLoad);
        }
        ChessMove move = moveCommand.getMove();
        ChessPiece piece = game.getChess().getBoard().getPiece(move.getStartPosition());
        ErrorMessage gameError = null;
        if (piece == null) {
            gameError = new ErrorMessage("Error: No piece at selected position.");
        }
        Collection<ChessMove> moves = game.getChess().validMoves(move.getStartPosition());
        if (!moves.contains(move)) {
            gameError = new ErrorMessage("Error: Not a valid move.");
            }
        if (gameError != null) {
            sendMessage(session.getRemote(), gameError);
            return;
        }
        try {
            game.getChess().makeMove(move);
            gameDAO.updateGame(moveCommand.getGameID(), new Gson().toJson(game.getChess()));
        } catch (InvalidMoveException ex) {
            ErrorMessage invalid = new ErrorMessage("Error: " + ex.getMessage());
            sendMessage(session.getRemote(), invalid);
            return;
        }
        String jsonGame = new Gson().toJson(game.getChess());
        LoadGameMessage loadGame = new LoadGameMessage(jsonGame);
        connections.broadcast(session, loadGame);
        String startStr = String.format("%c%d", 'a' + move.getStartPosition().getColumn() - 1, move.getStartPosition().getRow());
        String endStr = String.format("%c%d", 'a' + move.getEndPosition().getColumn() - 1, move.getEndPosition().getRow());
        String moved = String.format("'%s' moved a piece from <%s> to <%s>.", username, startStr, endStr);
        NotificationMessage notify = new NotificationMessage(moved);
        connections.broadcast(session, notify);
        sendMessage(session.getRemote(), loadGame);
        sendMessage(session.getRemote(), new NotificationMessage(String.format("You made a move from <%s> to <%s>", startStr, endStr)));
        String warning = null;
        if (game.getChess().isInCheck(ChessGame.TeamColor.WHITE)) {
            warning = String.format("'%s' is in check!", game.whiteUsername());
        } else if (game.getChess().isInCheck(ChessGame.TeamColor.BLACK)) {
            warning = String.format("'%s' is in check!", game.blackUsername());
        }
        if (warning != null) {
            NotificationMessage warningMessage = new NotificationMessage(warning);
            connections.broadcast(session, warningMessage);
            sendMessage(session.getRemote(), warningMessage);
        }
        String check = isCheckmate(moveCommand.getGameID());
        if (!Objects.equals(check, null)) {
            NotificationMessage notice = new NotificationMessage(check);
            connections.broadcast(session, notice);
            sendMessage(session.getRemote(), notice);
            gameDAO.endGame(moveCommand.getGameID());
            LoadGameMessage load = new LoadGameMessage(new Gson().toJson(game.getChess()));
            connections.broadcast(session, load);
            sendMessage(session.getRemote(), load);
        }


    }

    private String isStalemate(int gameID, ChessGame.TeamColor color) throws ResponseException {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData game = gameDAO.getGame(gameID);
        ChessGame chess = game.getChess();
        if (chess.isInStalemate(color)) {
            return "Stalemate! Game over.";
        }
        return null;
    }

    private String isCheckmate(int gameID) throws ResponseException {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData game = gameDAO.getGame(gameID);
        ChessGame chess = game.getChess();
        if (chess.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            String black = game.blackUsername();
            return String.format("'%s' is in checkmate! Game over.", black);
        } else if (chess.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            String white = game.whiteUsername();
            return String.format("'%s' is in checkmate! Game over.", white);
        }
        return null;
    }

    private ErrorMessage getErrorMessage(String username, GameData game) {
        ChessGame.TeamColor turn = game.getChess().getTeamTurn();
        ErrorMessage error = null;
        if ((Objects.equals(username, game.whiteUsername())) && (turn != ChessGame.TeamColor.WHITE)) {
            error = new ErrorMessage("Error: You are joined as white. It is the black team's turn.");
        } else if (Objects.equals(username, game.blackUsername()) && (turn != ChessGame.TeamColor.BLACK)) {
            error = new ErrorMessage("Error: You are joined as black. It is the white team's turn.");
        } else if (!(Objects.equals(username, game.blackUsername())) && !(Objects.equals(username, game.whiteUsername()))) {
            error = new ErrorMessage("Error: An observer cannot make moves.");
        } else if (game.getChess().isEnded()) {
            error = new ErrorMessage("Error: The game is ended. You can leave now.");
        }
        return error;
    }

    private void leave(Session session, LeaveCommand leaveCommand, String username) throws ResponseException, IOException {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData game = gameDAO.getGame(leaveCommand.getGameID());
        if (game == null) {
            ErrorMessage error = new ErrorMessage("Error: no game available to leave!");
            sendMessage(session.getRemote(), error);
            return;
        }
        NotificationMessage notify = new NotificationMessage(String.format("'%s' left the game", username));
        if (Objects.equals(username, game.whiteUsername())) {
            gameDAO.leaveGame(leaveCommand.getGameID(), "WHITE");
        } else if (Objects.equals(username, game.blackUsername())) {
            gameDAO.leaveGame(leaveCommand.getGameID(), "BLACK");
        }
        connections.remove(session);
        connections.broadcast(session, notify);
    }

    private void resign(Session session, ResignCommand resignCommand, String username) throws ResponseException, IOException {
        MySQLGameDAO gameDAO = new MySQLGameDAO();
        GameData game = gameDAO.getGame(resignCommand.getGameID());
        if (game == null) {
            ErrorMessage error = new ErrorMessage("Error: no game available to resign!");
            sendMessage(session.getRemote(), error);
            return;
        }
        if ((!Objects.equals(username, game.whiteUsername())) && (!Objects.equals(username, game.blackUsername()))) {
            ErrorMessage error = new ErrorMessage("Error: an observer cannot resign!");
            sendMessage(session.getRemote(), error);
        }
        gameDAO.endGame(resignCommand.getGameID());
        LoadGameMessage load = new LoadGameMessage(new Gson().toJson(game.getChess()));
        connections.broadcast(session, load);
        NotificationMessage notify = new NotificationMessage(String.format("'%s' resigned from the game! Game over.", username));
        connections.broadcast(session, notify);
    }
}
