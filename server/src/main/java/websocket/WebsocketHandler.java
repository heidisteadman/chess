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
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;

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
    public void handleClose(WsCloseContext ctx) {
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

    private void leave(Session session, LeaveCommand leaveCommand, String username) {}

    private void resign(Session session, ResignCommand resignCommand, String username) {}
}
