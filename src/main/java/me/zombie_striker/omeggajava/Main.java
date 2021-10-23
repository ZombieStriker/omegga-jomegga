package me.zombie_striker.omeggajava;


import com.sun.tools.javac.util.Pair;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import me.zombie_striker.omeggajava.events.*;
import me.zombie_striker.omeggajava.logic.LogicCore;
import me.zombie_striker.omeggajava.objects.Player;
import me.zombie_striker.omeggajava.plugins.PluginManager;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private static ConsoleInput consoleinput;
    private static ConsoleOutput consoleoutput;

    private static List<Event> events = new LinkedList<>();
    private static boolean callingEvents = false;

    private static List<Listener> listeners = new LinkedList<>();

    private static boolean running = true;

    private static final LinkedList<Player> players = new LinkedList<>();

    private static final HashMap<Long,RPCResponse> responseHandlers = new HashMap<>();

    private static PluginManager pluginmanager = new PluginManager();

    protected static void callResponse(long input, JSONRPC2Response response){
        if(responseHandlers.containsKey(input)){
            responseHandlers.remove(input).onResponse(response);
        }
    }
    protected static long registerResponseHandler(RPCResponse response){
        for(long i = 0; i < 999; i++){
            if(!responseHandlers.containsKey(i)){
                responseHandlers.put(i,response);
                return i;
            }
        }
        return -1;
    }

    public static void stop(){
        running = false;
    }
    public static boolean isRunning(){
        return running;
    }

    protected static void registerListener(Listener listener){
        listeners.add(listener);
    }
    protected static void unregisterListener(Listener listener){
        listeners.remove(listener);
    }

    protected static void callEvent(Event event){
        while(callingEvents){}
        events.add(event);
    }

    public static void main(String... arg) {
        consoleinput = new ConsoleInput();
        consoleoutput = new ConsoleOutput();

        LogicCore.init();
        pluginmanager.scanForJars(getJarFile().getParentFile().getParentFile(),getJarFile());
        pluginmanager.loadPlugins();

        while(running){
            tick();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        pluginmanager.shutdownPlugins();
    }

    private static void tick() {
        TickEvent tickEvent = new TickEvent();
        callEvent(tickEvent);
        for(Player player : players){
            player.updatePositon();
        }

        callingEvents=true;
        List<Event> ev = new ArrayList<>(events);
        events.clear();
        callingEvents=false;
        for(Event event : ev) {
            HashMap<EventPriority, List<Pair<Listener, Method>>> priorityList = new HashMap<>();

            for(EventPriority e : EventPriority.values()){
                priorityList.put(e,new LinkedList<>());
            }

            for (Listener listener : listeners) {
                Class klass = listener.getClass();
                for (Method method : klass.getMethods()) {
                    if (method.isAnnotationPresent(EventHandler.class)) {
                        if (method.getParameterCount() == 1 && (method.getParameterTypes()[0].isInstance(event))) {
                            priorityList.get(method.getAnnotation(EventHandler.class).priority()).add(new Pair<>(listener, method));
                        }
                    }
                }
            }

            for (EventPriority priority : EventPriority.values()) {
                for (Pair<Listener, Method> pair : priorityList.get(priority)) {
                    try {
                        pair.snd.invoke(pair.fst, event);
                    } catch (Exception e) {
                        JOmegga.log("METHOD ERR: " + pair.snd.getName() + "  : " + event.getClass().getName());
                        JOmegga.log("Error : " + e.getLocalizedMessage());
                        for (StackTraceElement s : e.getStackTrace()) {
                            JOmegga.log(s.getFileName() + " " + s.getClassName() + " " + s.getMethodName() + " " + s.getLineNumber());
                        }
                    }
                }
            }
        }
        consoleoutput.tick();
    }

    protected static File getJarFile(){
        try {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static ConsoleOutput getOutput() {
        return consoleoutput;
    }
    protected static ConsoleInput getInput(){
        return consoleinput;
    }
    public static void addPlayer(Player player){
        players.add(player);
    }
    public static boolean removePlayer(Player player){
        return players.remove(player);
    }

    public static List<Player> getPlayers() {
        return new LinkedList<>(players);
    }
}