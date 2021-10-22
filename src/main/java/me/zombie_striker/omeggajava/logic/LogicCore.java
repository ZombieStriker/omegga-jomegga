package me.zombie_striker.omeggajava.logic;

import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.logic.listeners.*;

public class LogicCore {

    public static void init(){
        JOmegga.registerListener(new InitListener());
        JOmegga.registerListener(new JoinListener());
        JOmegga.registerListener(new StopListener());
        JOmegga.registerListener(new ChatListener());
        JOmegga.registerListener(new RPCListener());
    }
}
