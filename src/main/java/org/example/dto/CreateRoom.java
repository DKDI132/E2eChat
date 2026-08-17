package org.example.dto;
import lombok.Builder;

@Builder
public record CreateRoom(String nickname,String roomName,String password,String clientId,String key) {

}
