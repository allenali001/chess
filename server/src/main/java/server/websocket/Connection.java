package server.websocket;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String playerUsername;
    public Session session;

    public Connection(String playerUsername, Session session) {
        this.playerUsername = playerUsername;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}