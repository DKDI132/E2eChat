package org.example.entity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;



@Builder
@Getter
public class User {
    private final String id;
    private final String nickname;
    private final WebSocketSession session;
    private final boolean isAdmin;
    private final String ecdhPub;
}
