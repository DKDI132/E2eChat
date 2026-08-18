package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Room;
import org.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public class WebSocketService {

    @Autowired
    private ObjectMapper objectMapper;

    public void sendJson(WebSocketSession session, Object data) {
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(data);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(Room room, Object data) {
        for (User user : room.getUsers().values()) {
            sendJson(user.getSession(), data);
        }
    }

    public void closeSessionSafely(WebSocketSession session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}