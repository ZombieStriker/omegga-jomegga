package me.zombie_striker.omeggajava.events;

import me.zombie_striker.omeggajava.objects.Player;

public class JoinEvent implements Event{

    private Player player;

    public JoinEvent(Player player){
    this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
