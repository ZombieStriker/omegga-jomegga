package me.zombie_striker.omeggajava.logic.listeners;

import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.events.EventHandler;
import me.zombie_striker.omeggajava.events.Listener;
import me.zombie_striker.omeggajava.events.StopEvent;

public class StopListener implements Listener {

    @EventHandler
    public void onStop(StopEvent stopEvent){
        JOmegga.log("Stopping...");
        JOmegga.stop();
    }

}
