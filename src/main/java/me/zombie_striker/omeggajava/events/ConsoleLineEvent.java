package me.zombie_striker.omeggajava.events;

public class ConsoleLineEvent implements Event {

    private String log;

    public ConsoleLineEvent(String log){
        this.log = log;
    }

    public String getLog() {
        return log;
    }
}
