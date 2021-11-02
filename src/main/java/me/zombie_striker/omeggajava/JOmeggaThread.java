package me.zombie_striker.omeggajava;

import com.sun.tools.javac.util.Pair;
import me.zombie_striker.omeggajava.events.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class JOmeggaThread implements Runnable {

    private Thread thread;
    protected static boolean callingEvents = false;

    public JOmeggaThread(){
        thread = new Thread(this);
    }

    public void start(){
        thread.start();
    }

    @Deprecated
    public void stop(){
        thread.stop();
    }

    @Override
    public void run() {
        while(JOmegga.isRunning()){
            tick();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        JOmegga.getInput().stop();
        stop();;
    }


    private static void tick() {
        TickEvent tickEvent = new TickEvent();
        JOmegga.callEvent(tickEvent);
        callingEvents=true;
        List<Event> ev = new ArrayList<>();
        for(int e = 0; e < JOmegga.getEvents().size(); e++){
            Event ee = JOmegga.getEvents().get(e);
                ev.add(ee);
        }
        JOmegga.getEvents().clear();
        callingEvents=false;
        for(Event event : ev) {
            HashMap<EventPriority, List<Pair<Listener, Method>>> priorityList = new HashMap<>();

            for(EventPriority e : EventPriority.values()){
                priorityList.put(e,new LinkedList<>());
            }
            List<Listener> list = new ArrayList<>();
            for(int l = 0; l < JOmegga.getListeners().size(); l++){
               Listener ll = JOmegga.getListeners().get(l);
               list.add(ll);
            }

            for (Listener listener : list) {
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
        JOmegga.getOutput().tick();
    }
}
