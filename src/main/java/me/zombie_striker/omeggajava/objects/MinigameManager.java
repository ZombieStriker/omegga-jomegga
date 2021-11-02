package me.zombie_striker.omeggajava.objects;

import java.util.ArrayList;
import java.util.List;

public class MinigameManager {

    private List<Minigame> minigames = new ArrayList<>();

    public List<Minigame> getMinigames(){
        return minigames;
    }
    public Minigame getMinigame(String name){
        for(Minigame minigame : minigames){
            if(minigame.getName().equals(name))
                return minigame;
        }
        return null;
    }

    public void registerMinigame(Minigame minigame) {minigames.add(minigame);
    }
}
