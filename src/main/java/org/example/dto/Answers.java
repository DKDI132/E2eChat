package org.example.dto;

import java.util.List;
import java.util.Map;

public class Answers {

    public static Map<String, Object> roomCreated(String roomId, String roomName) {
        return Map.of(
                "type", "room_created",
                "room_id", roomId,
                "room_name", roomName
        );
    }

    public static Map<String, Object> waitingApproval() {
        return Map.of("type", "waiting_approval");
    }

    public static Map<String, Object> newJoinRequest(String clientId, String nickname, String ecdhPub) {
        return Map.of(
                "type", "new_join_request",
                "client_id", clientId,
                "nickname", nickname,
                "ecdh_pub", ecdhPub
        );
    }

    public static Map<String, Object> joinApproved(String roomId, String roomName, String encryptedKey, String iv, String adminEcdhPub) {
        return Map.of(
                "type", "join_approved",
                "room_id", roomId,
                "room_name", roomName,
                "encrypted_room_key", encryptedKey,
                "iv", iv,
                "admin_ecdh_pub", adminEcdhPub
        );
    }

    public static Map<String, Object> joinRejected(String message) {
        return Map.of(
                "type", "join_rejected",
                "message", message
        );
    }

    public static Map<String, Object> chatBroadcast(String senderId, String nickname, String ciphertext, String iv) {
        return Map.of(
                "type", "chat_broadcast",
                "sender_id", senderId,
                "nickname", nickname,
                "ciphertext", ciphertext,
                "iv", iv
        );
    }

    public static Map<String, Object> membersUpdate(List<Map<String, Object>> members) {
        return Map.of(
                "type", "members_update",
                "members", members
        );
    }
}