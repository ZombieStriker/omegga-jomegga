package com.kmschr.brs;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BrickOwner extends User {
    int brickcount;

    public BrickOwner(@NotNull User user, int brickcount) {
        super(user.id, user.name);
        this.brickcount = brickcount;
    }

    public BrickOwner(UUID id, String name, int brickcount) {
        super(id, name);
        this.brickcount = brickcount;
    }

    public int getBrickcount() {
        return brickcount;
    }

}
