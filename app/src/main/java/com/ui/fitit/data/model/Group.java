package com.ui.fitit.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Group {
    final String id = UUID.randomUUID().toString();
    String name;
    List<String> users;
    Map<String, String> messages;

    public Group(String name) {
        this.name = name;
        this.users = new ArrayList<>();
        this.messages = new HashMap<>();
    }

    public void addUser(String username) {
        if (!users.contains(username)) {
            users.add(username);
        }
    }

    public boolean hasUser(String username) {
        return users.contains(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }
}
