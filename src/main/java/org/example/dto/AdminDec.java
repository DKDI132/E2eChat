package org.example.dto;
import lombok.Builder;


@Builder
public record AdminDec(String roomId,String targetClientId,boolean approved,String encryptedRoomKey,String iv,String adminPubKey) {
}
