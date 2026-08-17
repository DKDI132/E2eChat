package org.example.entity;
import lombok.Builder;
import lombok.Getter;

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
}
