package com.ui.fitit.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Group implements Serializable {
    final String id = UUID.randomUUID().toString();
    String name;
    List<String> users;

    public Group(String name) {
        this.name = name;
        this.users = new ArrayList<>();
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
