package websocket;

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
            throw new ResponseException(500, "Error: Game ID not associated with existing game.");
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

    public void makeMove(Session session, MakeMoveCommand moveCommand, String username) {}

    private void leave(Session session, LeaveCommand leaveCommand, String username) {}

    private void resign(Session session, ResignCommand resignCommand, String username) {}
}
