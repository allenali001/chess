package ui.websocket;

import webSocketMessages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}
