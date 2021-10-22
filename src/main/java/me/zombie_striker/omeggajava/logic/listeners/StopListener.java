package me.zombie_striker.omeggajava.logic.listeners;

import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.Main;
import me.zombie_striker.omeggajava.events.*;

public class StopListener implements Listener {

    @EventHandler(priority = EventPriority.JOMEGGA_MONITOR)
    public void onStop(StopEvent event){
        JOmegga.log("JOmegga detected the stop request.");
        Main.stop();
    }
}
