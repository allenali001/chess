package ui.websocket;

import websocketmesssages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}
