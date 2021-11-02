package me.zombie_striker.omeggajava.objects;

import java.util.ArrayList;
import java.util.List;

public class Minigame {

    private List<Team> teams = new ArrayList<>();

    private String name;

    public Minigame(String name){
        this.name = name;
    }

    public List<Team> getTeam(){
        return teams;
    }
    public Team getTeam(String name){
        for(Team team : teams){
            if(team.getName().equals(name))
                return team;
        }
        return null;
    }

    public String getName() {
        return name;
    }
    public void registerTeam(Team team){
        this.teams.add(team);
    }
}
