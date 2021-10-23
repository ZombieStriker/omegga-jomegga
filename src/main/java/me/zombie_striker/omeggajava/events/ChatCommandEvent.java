package me.zombie_striker.omeggajava.events;

public class ChatCommandEvent implements Event{

    private String message;
    private String[] args;
    private String command;
    private String playername;

    public ChatCommandEvent(String playername, String command, String message){
        this.playername = playername;
        this.message = message;
        this.command = command;
        if(message!=null) {
            this.args = message.split(" ");
        }else{
            this.args=new String[0];
        }
    }

    public String getCommand(){
        return command;
    }
    public String[] getArgs(){
        return args;
    }

    public String getMessage() {
        return message;
    }

    public String getPlayername() {
        return playername;
    }
}
