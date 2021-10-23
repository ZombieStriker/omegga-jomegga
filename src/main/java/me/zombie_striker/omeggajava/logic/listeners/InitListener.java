package me.zombie_striker.omeggajava.logic.listeners;

import com.kmschr.brs.BRS;
import com.kmschr.brs.SaveData;
import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.events.EventHandler;
import me.zombie_striker.omeggajava.events.EventPriority;
import me.zombie_striker.omeggajava.events.InitEvent;
import me.zombie_striker.omeggajava.events.Listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class InitListener implements Listener {

    @EventHandler(priority = EventPriority.JOMEGGA_INIT)
    public void onInit(InitEvent event){
        JOmegga.log("JOmegga is starting");
    }
}
