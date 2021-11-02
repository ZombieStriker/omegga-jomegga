package me.zombie_striker.omeggajava.events;

import me.zombie_striker.omeggajava.objects.Player;

public class PlayerLeaveEvent implements Event{

    private Player player;

    public PlayerLeaveEvent(Player player){
    this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
