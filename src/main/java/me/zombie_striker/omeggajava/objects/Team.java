package me.zombie_striker.omeggajava.objects;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private List<Player> players = new ArrayList<>();

    private String name;

    public Team(String name){
        this.name= name;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }
    public boolean contains(Player player){
        return players.contains(player);
    }
}
