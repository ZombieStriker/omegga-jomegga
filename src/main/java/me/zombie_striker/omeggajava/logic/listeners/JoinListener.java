package me.zombie_striker.omeggajava.logic.listeners;

import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.events.*;

public class JoinListener implements Listener {

    @EventHandler(priority = EventPriority.JOMEGGA_INIT)
    public void onJoin(JoinEvent event){
        JOmegga.log("JOmegga detected player "+event.getPlayer().getName()+" joining.");
    }
}
