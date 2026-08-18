package org.example.entity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Builder
@Getter
public class Room {
    private final String id;
    private final String name;
    private final String password;
    private final String adminId;
    @Builder.Default
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    @Builder.Default
    private ConcurrentHashMap<String, User> pendingReq = new ConcurrentHashMap<>();

    public void addUser(String name,User user){
        this.users.put(name,user);
    }
    public void addPendingReq(String name,User user){
        this.pendingReq.put(name,user);
    }
    public User getAdmin(){
        User admin = this.users.get(adminId);
        return admin;
    }
    public void removePendingUser(String name){
        this.pendingReq.remove(name);
    }
    public void removeUser(String name){
        this.users.remove(name);
    }
    public User getPendingUser(String name){
        User user = pendingReq.get(name);
        return user;
    }
    public User getUser(String name){
        User user = users.get(name);
        return user;
    }
    public boolean existingPendingUser(String name){
        User user = pendingReq.get(name);
        if (user==null) return false;
        return true;

    }
    public List<Map<String, Object>> getMembersPayload() {
        return this.users.values().stream()
                .map(user -> Map.<String, Object>of(
                        "id", user.getId(),
                        "nickname", user.getNickname(),
                        "is_admin", user.isAdmin()
                ))
                .toList();
    }
}
