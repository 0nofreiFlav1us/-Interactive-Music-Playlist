package com.onofreiflavius.music.controller.websocket;

import com.onofreiflavius.music.model.services.Services;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private String currentSongPlaying;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessions.add(session);
        System.out.println(session + " connected");
        if (currentSongPlaying != null) {
            session.sendMessage(new TextMessage(currentSongPlaying));
        }

        System.out.println(currentSongPlaying);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String clientMessage = message.getPayload();
        broadcast(clientMessage);

        System.out.println(clientMessage);
        if (!clientMessage.equals("new_song")) {
            currentSongPlaying = clientMessage;
        }
        System.out.println(currentSongPlaying);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
        System.out.println(session + " disconnected");
    }

    private void broadcast(String message) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }

}
