package me.zombie_striker.omeggajava.logic.listeners;

import me.zombie_striker.omeggajava.events.ChatEvent;
import me.zombie_striker.omeggajava.events.EventHandler;
import me.zombie_striker.omeggajava.events.EventPriority;
import me.zombie_striker.omeggajava.events.Listener;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.JOMEGGA_MONITOR)
    public void onChat(ChatEvent event){
    }
}
