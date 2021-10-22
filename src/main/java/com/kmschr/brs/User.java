package com.kmschr.brs;

import java.util.UUID;

/**
 * Represents a Brickadia player. Each player has a unique id, which can be viewed in the url of your profile on
 * https://www.brickadia.com.
 */
public class User {
    UUID id;
    String name;

    public User(String uuid, String name) {
        this.id = UUID.fromString(uuid);
        this.name = name;
    }

    public User(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && name.equals(user.name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
