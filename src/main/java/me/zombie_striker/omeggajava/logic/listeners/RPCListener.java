package me.zombie_striker.omeggajava.logic.listeners;

import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.Main;
import me.zombie_striker.omeggajava.RPCResponse;
import me.zombie_striker.omeggajava.events.*;
import me.zombie_striker.omeggajava.objects.Player;
import me.zombie_striker.omeggajava.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class RPCListener implements Listener {

    @EventHandler(priority = EventPriority.JOMEGGA_INIT)
    public void onNotification(RPCNotificationEvent event){
        HashMap<String, Object> map = JsonHelper.convertJsonToHashMap(event.getNotification().getParams());
        /*JOmegga.broadcast("Notification ("+event.getNotification().getMethod()+") :");
        for(Map.Entry<String, Object> e : map.entrySet()){
            JOmegga.log("---"+e.getKey()+": "+e.getValue());
        }*/
        if(map == null){
            JOmegga.log("Map is null");
            return;
        }
        try {
        if (event.getNotification().getMethod().equals("join")) {
            Player player = new Player((String) map.get("0.name"), (String) map.get("0.id"), (String) map.get("0.state"), (String) map.get("0.controller"));
            JoinEvent joinEvent = new JoinEvent(player);
            JOmegga.callEvent(joinEvent);
            Main.addPlayer(player);
        } else if (event.getNotification().getMethod().equals("leave")) {
            for(Player player : Main.getPlayers()){
                if(player.getName().equals(map.get("0.name"))
                        &&player.getId().equals(map.get("0.id"))
                        &&player.getController().equals(map.get("0.controller"))){
                    Main.removePlayer(player);
                    break;
                }
            }
        } else if (event.getNotification().getMethod().equals("chat")){
            ChatEvent chatevent = new ChatEvent((String)map.get("0"),(String)map.get("1"));
            JOmegga.callEvent(chatevent);
        } else if (event.getNotification().getMethod().startsWith("chatcmd:")){
            ChatCommandEvent chatevent = new ChatCommandEvent((String)map.get("0"),event.getNotification().getMethod().substring("chatcmd:".length()),(String)map.get("1"));
            JOmegga.callEvent(chatevent);
        } else if (event.getNotification().getMethod().startsWith("cmd:")){
            String command = event.getNotification().getMethod().substring("cmd:".length());
            String message = (String) map.get("0");
            String playername= (String) map.get("1");
            CommandEvent chatevent = new CommandEvent(playername,command,message);
            JOmegga.callEvent(chatevent);
        } else if (event.getNotification().getMethod().equals("line")){

            Object obj = map.get("0");
            if(obj instanceof String) {
                ConsoleLineEvent chatevent = new ConsoleLineEvent((String) obj);
                JOmegga.callEvent(chatevent);
            }else if (obj == null){
                JOmegga.log("Brickadia log is null: "+map.keySet());
            }else{
                JOmegga.log("Brickadia log "+obj.getClass().getName());
            }
        }
        } catch (Exception e) {
            JOmegga.log("Error : "+e.getLocalizedMessage());
            for(StackTraceElement s : e.getStackTrace()){
                JOmegga.log(s.getClassName()+" "+s.getMethodName()+" "+s.getLineNumber());
            }
        }
    }
}
