package org.example.dto;
import lombok.Builder;



@Builder
public record ChatMessage(String roomId,String clientId,String ciphertext,String iv) {
}
