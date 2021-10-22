package me.zombie_striker.omeggajava.logic.listeners;

import com.kmschr.brs.SaveData;
import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.RPCResponse;
import me.zombie_striker.omeggajava.events.ChatEvent;
import me.zombie_striker.omeggajava.events.EventHandler;
import me.zombie_striker.omeggajava.events.EventPriority;
import me.zombie_striker.omeggajava.events.Listener;
import net.minidev.json.JSONObject;

public class ChatListener implements Listener {

    private RPCResponse response;

    @EventHandler(priority = EventPriority.JOMEGGA_MONITOR)
    public void onChat(ChatEvent event){
    }
}
