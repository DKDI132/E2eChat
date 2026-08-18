package org.example.services;

import org.example.dto.*;
import org.example.entity.Room;
import org.example.entity.User;
import org.example.exceptions.InvalidPasswordException;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomsServ {
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private final WebSocketService wsService;

    public RoomsServ(WebSocketService wsService) {
        this.wsService = wsService;
    }

    public Room createRoom(CreateRoom dto, WebSocketSession session) {
        String roomId = "room-" + UUID.randomUUID().toString().substring(0, 8);
        User admin = User.builder()
                .id(dto.clientId())
                .nickname(dto.nickname())
                .session(session)
                .isAdmin(true)
                .ecdhPub(dto.key())
                .build();

        Room room = Room.builder()
                .id(roomId)
                .name(dto.roomName())
                .password(dto.password())
                .adminId(dto.clientId())
                .build();

        room.addUser(dto.clientId(), admin);
        this.rooms.put(roomId, room);

        wsService.sendJson(session, Answers.roomCreated(roomId, dto.roomName()));
        return room;
    }

    public User requestJoin(JoinRoom dto, WebSocketSession session) {
        Room room = this.rooms.get(dto.roomId());
        if (room == null) {
            throw new IllegalArgumentException("Not existing");
        }
        if (!room.getPassword().equals(dto.password())) {
            throw new InvalidPasswordException("Bad credentials");
        }

        User user = User.builder()
                .id(dto.clientId())
                .nickname(dto.nickname())
                .session(session)
                .isAdmin(false)
                .ecdhPub(dto.key())
                .build();

        room.addPendingReq(dto.clientId(), user);
        wsService.sendJson(session, Answers.waitingApproval());
        wsService.sendJson(room.getAdmin().getSession(), Answers.newJoinRequest(dto.clientId(), dto.nickname(), dto.key()));
        return user;
    }

    public void handleAdminDec(AdminDec dto, WebSocketSession session) {
        Room room = this.rooms.get(dto.roomId());
        if (room == null || !room.existingPendingUser(dto.targetClientId())) {
            throw new IllegalArgumentException("Not existing");
        }

        User admin = room.getAdmin();
        User target = room.getPendingUser(dto.targetClientId());

        if (!admin.getSession().getId().equals(session.getId())) {
            throw new SecurityException("Unauthorized");
        }

        room.removePendingUser(dto.targetClientId());

        if (!dto.approved()) {
            wsService.sendJson(target.getSession(), Answers.joinRejected("Admin odrzucił Twoją prośbę"));
            wsService.closeSessionSafely(target.getSession());
        } else {
            room.addUser(dto.targetClientId(), target);
            wsService.sendJson(target.getSession(), Answers.joinApproved(
                    room.getId(),
                    room.getName(),
                    dto.encryptedRoomKey(),
                    dto.iv(),
                    dto.adminPubKey()
            ));
            wsService.broadcast(room, Answers.membersUpdate(room.getMembersPayload()));
        }
    }

    public void chatMessage(ChatMessage dto, WebSocketSession session) {
        Room room = rooms.get(dto.roomId());
        if (room == null) {
            throw new IllegalArgumentException("Not existing");
        }

        User target = room.getUser(dto.clientId());
        if (target == null || !target.getSession().getId().equals(session.getId())) {
            throw new SecurityException("Unauthorized");
        }

        wsService.broadcast(room, Answers.chatBroadcast(target.getId(), target.getNickname(), dto.ciphertext(), dto.iv()));
    }

    public void handleDisconnect(WebSocketSession session) {
        for (Room room : rooms.values()) {

            if (room.getAdmin() != null && room.getAdmin().getSession().getId().equals(session.getId())) {
                wsService.broadcast(room, Map.of(
                        "type", "room_closed",
                        "message", "Pokój został zamknięty (Admin rozłączył się, dane z RAM wyczyszczone)."
                ));
                rooms.remove(room.getId());
                return;
            }

            for (User user : room.getUsers().values()) {
                if (user.getSession().getId().equals(session.getId())) {
                    room.removeUser(user.getId());
                    wsService.broadcast(room, Answers.membersUpdate(room.getMembersPayload()));
                    return;
                }
            }
        }
    }
}
