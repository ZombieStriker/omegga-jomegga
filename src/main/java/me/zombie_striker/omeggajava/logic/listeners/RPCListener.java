package me.zombie_striker.omeggajava.logic.listeners;

import me.zombie_striker.omeggajava.JOmegga;
import me.zombie_striker.omeggajava.events.*;
import me.zombie_striker.omeggajava.objects.Player;
import me.zombie_striker.omeggajava.util.JsonHelper;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RPCListener implements Listener {

    @EventHandler(priority = EventPriority.JOMEGGA_INIT)
    public void onNotification(RPCNotificationEvent event) {
        HashMap<String, Object> map = JsonHelper.convertJsonToHashMap(event.getNotification().getParams());
        /*JOmegga.broadcast("Notification ("+event.getNotification().getMethod()+") :");
        for(Map.Entry<String, Object> e : map.entrySet()){
            JOmegga.log("---"+e.getKey()+": "+e.getValue());
        }*/
        if (map == null) {
            JOmegga.log("Map is null");
            return;
        }
        try {
            if (event.getNotification().getMethod().equals("join")) {
                if (JOmegga.getPlayer((String) map.get("0.name")) == null) {
                    Player player = new Player((String) map.get("0.name"), (String) map.get("0.id"), (String) map.get("0.state"), (String) map.get("0.controller"));
                    JoinEvent joinEvent = new JoinEvent(player);
                    JOmegga.callEvent(joinEvent);
                    JOmegga.addPlayer(player);
                }
            } else if (event.getNotification().getMethod().equals("leave")) {
                for (Player player : JOmegga.getPlayers()) {
                    if (player.getName().equals(map.get("0.name"))
                            && player.getId().equals(map.get("0.id"))
                            && player.getController().equals(map.get("0.controller"))) {
                        PlayerLeaveEvent joinEvent = new PlayerLeaveEvent(player);
                        JOmegga.callEvent(joinEvent);
                        JOmegga.removePlayer(player);
                        break;
                    }
                }
            } else if (event.getNotification().getMethod().equals("host")) {
                JOmegga.setHostUUID(UUID.fromString((String) map.get("0.id")));

            } else if (event.getNotification().getMethod().equals("plugin:status")) {
                String name = (String) map.get("0");
                if (name.equalsIgnoreCase(JOmegga.getPluginName())) {
                    //TODO: Check if plugins are installed
                }
            } else if (event.getNotification().getMethod().equals("bootstrap")) {
                BootstrapEvent chatevent = new BootstrapEvent();
                JOmegga.callEvent(chatevent);
            } else if (event.getNotification().getMethod().equalsIgnoreCase("plugin:players:raw")) {
                int entries = map.keySet().size() / 4;
                for (int i = 0; i < entries; i++) {
                    String name = (String) map.get("0." + i + ".0");
                    String id = (String) map.get("0." + i + ".1");
                    String state = (String) map.get("0." + i + ".3");
                    String controller = (String) map.get("0." + i + ".2");
                    boolean found = false;
                    for (Player player : JOmegga.getPlayers()) {
                        if (player.getName().equals(name) && player.getId().equals(id)) {
                            found = true;
                            if (controller == null || controller.equals("null")) {
                                JOmegga.log("CONTROLER FOR " + name + " IS NULL");
                                controller = null;
                            }
                            player.setController(controller);
                            player.setState(state);
                            JOmegga.log("Updating existing player: " + name + ":" + id + " " + state + " " + controller);
                            break;
                        }
                    }
                    if (!found) {
                        JOmegga.addPlayer(new Player(name, id, state, controller));
                        JOmegga.log("Registering existing player: " + name + ":" + id + " " + state + " " + controller);
                    }
                }
            } else if (event.getNotification().getMethod().equals("start")) {
                StartEvent startEvent = new StartEvent();
                JOmegga.callEvent(startEvent);
            } else if (event.getNotification().getMethod().equals("chat")) {
                ChatEvent chatevent = new ChatEvent((String) map.get("0"), (String) map.get("1"));
                JOmegga.callEvent(chatevent);
            } else if (event.getNotification().getMethod().startsWith("metrics:heartbeat")) {
                for(Map.Entry<String, Object> ent : map.entrySet()){
                    //TODO: Update player information.
                    if(ent.getKey().startsWith("0.bricks")){
                        JOmegga.setBrickcount((long) ent.getValue());
                    }else if(ent.getKey().startsWith("0.serverName")){
                        JOmegga.setServername((String) ent.getValue());
                    }else if(ent.getKey().startsWith("0.description")){
                        JOmegga.setDescription((String) ent.getValue());
                    }
                }

            } else if (event.getNotification().getMethod().startsWith("chatcmd")) {
                JOmegga.log("Chat Command: " + event.getNotification().getMethod()+" ["+map.get("0")+" "+map.get("1")+map.get("2")+"]");
                StringBuilder args = new StringBuilder();
                for(int i = 2; i < map.size()&&i<50; i++){
                    if(map.containsKey(i+"") && ((String)map.get(i+"")).length()>0) {
                        args.append(map.get(i + ""));
                        args.append(" ");
                    }
                }
                ChatCommandEvent chatevent = new ChatCommandEvent((String) map.get("1"), ((String) map.get("0")), args.toString());
                JOmegga.callEvent(chatevent);
            } else if (event.getNotification().getMethod().startsWith("cmd")) {
            String command = event.getNotification().getMethod().substring("cmd".length());
            String message = (String) map.get("0");
            String playername = (String) map.get("1");
            CommandEvent chatevent = new CommandEvent(playername, command, message);
            JOmegga.callEvent(chatevent);
        } else if (event.getNotification().getMethod().equals("line")) {

            Object obj = map.get("0");
            if (obj instanceof String) {
                ConsoleLineEvent chatevent = new ConsoleLineEvent((String) obj);
                JOmegga.callEvent(chatevent);
            } else if (obj == null) {
                JOmegga.log("Brickadia log is null: " + map.keySet());
            } else {
                JOmegga.log("Brickadia log " + obj.getClass().getName());
            }
        } else {
            JOmegga.log("ERROR ON NOTIFICATION " + event.getNotification().getMethod() + " : " + event.getNotification().getParams().toString());
        }

    } catch(
    Exception e)

    {
        JOmegga.log("Error : " + e.getLocalizedMessage());
        for (StackTraceElement s : e.getStackTrace()) {
            JOmegga.log(s.getClassName() + " " + s.getMethodName() + " " + s.getLineNumber());
        }
    }
}
}
