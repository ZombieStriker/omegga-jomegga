package me.zombie_striker.omeggajava.events;

public class RPCReadEvent implements Event {

    private String raw;

    public RPCReadEvent(String raw){
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

}
