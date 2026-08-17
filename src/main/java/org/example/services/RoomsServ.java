package org.example.services;


import org.example.dto.AdminDec;
import org.example.dto.CreateRoom;
import org.example.dto.JoinRoom;
import org.example.entity.Room;
import org.example.entity.User;
import org.example.exceptions.InvalidPasswordException;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.naming.AuthenticationException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomsServ {
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom(CreateRoom dto, WebSocketSession session){
        String roomId = "room"+ UUID.randomUUID().toString().substring(0,8);
        User admin = User.builder().id(dto.clientId()).nickname(dto.nickname()).session(session).isAdmin(true).ecdhPub(dto.key()).build();
        Room room = Room.builder().id(roomId).name(dto.roomName()).password(dto.password()).adminId(dto.clientId()).build();
        room.addUser(dto.clientId(),admin);
        this.rooms.put(roomId,room);
        return room;
    }
    public User requestJoin(JoinRoom dto, WebSocketSession session){
        Room room = this.rooms.get(dto.roomId());
        if (room == null){
            throw new IllegalArgumentException("Not existing");
        }
        if(!room.getPassword().equals(dto.password())){
            throw new InvalidPasswordException("Bad credentials");
        }
        User user = User.builder().id(dto.clientId()).nickname(dto.nickname()).session(session).isAdmin(false).ecdhPub(dto.key()).build();
        room.addPendingReq(dto.clientId(),user);
        return user;
    }
    public Boolean handleAdminDec(AdminDec dto,WebSocketSession session){
        Room room = this.rooms.get(dto.roomId());
        User admin = room.getAdmin();

    }

}
