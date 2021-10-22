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



        File scifidoor = new File(JOmegga.getOmeggaDir(),"ore_1.brs");
        File output = new File(JOmegga.getOmeggaDir(),"out.brs");

        if(!scifidoor.exists()){
            JOmegga.broadcast("Scifi door does not exist");
            return;
        }
        try {
            JOmegga.log("Attempting to read save");
            SaveData save = BRS.readSave(new FileInputStream(scifidoor));
            JOmegga.log("Attempt to save save");
            BRS.writeSave(new FileOutputStream(output),save);
        } catch (Exception|Error e) {
            JOmegga.log("Failed to read and write save: ");
            for(StackTraceElement s : e.getStackTrace()){
                JOmegga.log(s.getClassName()+" "+s.getMethodName()+" "+s.getLineNumber());
            }
        }


    }
}
