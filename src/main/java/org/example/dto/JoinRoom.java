package org.example.dto;
import lombok.Builder;

@Builder
public record JoinRoom(String roomId,String nickname,String password,String clientId,String key) {
}
