package me.zombie_striker.omeggajava.events;

public class ChatEvent implements Event{

    private String message;
    private String playername;

    public ChatEvent(String playername, String message){
        this.playername = playername;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getPlayername() {
        return playername;
    }
}
