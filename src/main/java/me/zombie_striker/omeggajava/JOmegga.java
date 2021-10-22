package me.zombie_striker.omeggajava;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import me.zombie_striker.omeggajava.events.Event;
import me.zombie_striker.omeggajava.events.Listener;
import me.zombie_striker.omeggajava.objects.Player;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JOmegga {

    public static File getJOmeggaJar(){
        return Main.getJarFile();
    }
    public static File getOmeggaDir(){
        return Main.getJarFile().getParentFile().getParentFile().getParentFile();
    }

    public static void log(String log){
        JSONRPC2Notification notification = new JSONRPC2Notification("log",Arrays.asList(log));
        Main.getOutput().addToQueue(notification);
    }
    public static void broadcast(String message){
        JSONRPC2Notification notification = new JSONRPC2Notification("broadcast", Arrays.asList(message));
        Main.getOutput().addToQueue(notification);
    }
    public static void exec(String message){
        JSONRPC2Notification notification = new JSONRPC2Notification("exec", Arrays.asList(message));
        Main.getOutput().addToQueue(notification);
    }
    public static void whisper(Player player, String message){
        HashMap<String,Object> params = new HashMap<>();
        params.put("target",player.getName());
        params.put("line",message);
        JSONRPC2Notification notification = new JSONRPC2Notification("whisper", params);
        Main.getOutput().addToQueue(notification);
    }

    public static void callEvent(Event event){
        Main.callEvent(event);
    }
    public static void registerListener(Listener listener){
        Main.registerListener(listener);
    }
    public static void unregisterListener(Listener listener){
        Main.unregisterListener(listener);
    }
    public static void sendRPCNotification(String method, Object... params){
        JSONRPC2Notification notification = new JSONRPC2Notification(method,Arrays.asList(params));
        Main.getOutput().addToQueue(notification);
    }
    public static void getRPCValue(RPCResponse responseHandler, String method, HashMap<String,Object> namedParams){
        long id = Main.registerResponseHandler(responseHandler);
        JSONRPC2Request request = new JSONRPC2Request(method,namedParams,id);
        Main.getOutput().addToQueue(request);
    }
    public static void getRPCValue(RPCResponse responseHandler, String method, String param){
        long id = Main.registerResponseHandler(responseHandler);
        JSONRPC2Request request = new JSONRPC2Request(method,Arrays.asList(param),id);
        Main.getOutput().addToQueue(request);
    }

    public static void sendRPCResponse(JSONRPC2Response response) {
        Main.getOutput().addToQueue(response);
    }
    public static Player getPlayer(String name){
        for(Player player : Main.getPlayers()){
            if(player.getName().equals(name))
                return player;
        }
        return null;
    }
    public static List<Player> getPlayers(){
        return Main.getPlayers();
    }
}
