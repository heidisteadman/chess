package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, int gameID, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (var entry : connections.entrySet()) {
            Session session = entry.getKey();
            int sessionGameID = entry.getValue();
            if (session.isOpen() && !session.equals(excludeSession) && sessionGameID==gameID) {
                session.getRemote().sendString(msg);
            }
        }
    }
}
